# springboot-security-concise


> SpringSecurity主要实现UserDetailsService来验证登录的用户信息，和Security的配置类来对登录方式和资源进行限制。
> 案例包含利用数据库进行登录验证、URL访问限制、自定义登录页和利用ajax方式登录、实现自定义过滤器对验证码进行验证

## SprigSecurity接口
### UserDetails

- 接口：表示用户信息，账号：密码：是否过期：是否锁定：证书是否过期：权限集合
- 实现类：User
  自定义类实现UserDetails接口，作为系统中的用户类，这个类可以交给SpringSecurity使用
```
需要自定义的User类继承UserDetails，然后实现方法，但在某些案例中也没有继承
同时在数据库中添加相应字段，如是否过期是否锁定等
```

```java
/**
 * 用户表
 * 用户表和角色表的对应关系，
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
// 自定义的User可以实现 implements UserDetails 接口，需要完成方法如是否可用，是否锁定，是否过期，角色集合等，同时在数据库中添加这些字段
// 实现这个方法可用于扩展，也可以不实现
public class User implements UserDetails{

    @Id
    // 主键自动增长
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    // 过期
    @Column(name = "isAccountNonExpired")
    Boolean isAccountNonExpired;
    // 锁定
    @Column(name = "isAccountNonLocked")
    Boolean isAccountNonLocked;
    // 凭证
    @Column(name = "isCredentialsNonExpired")
    Boolean isCredentialsNonExpired;
    // 启用
    @Column(name = "isEnabled")
    Boolean isEnabled;
    // 权限
    // List<GrantedAuthority> authorities;


    /**
     * 多对多关系会在创建用户和新角色时级联新增，关联表为user_role，当前对象在关联表对应的外键，和另一方在关联表中对应的外键
     * cascade:级联操作，如保存、删除时级联的行为
     * joinColumns:在关联表中的外键名
     * inverseJoinColumns:另一方在关联表中的外键名
     */
    @ManyToMany(targetEntity = Role.class, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "u_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "r_id", referencedColumnName = "id")})
    List<Role> roles = new ArrayList<>();


    /**
     * 重写toString()方法，否则在sout输出时，会导致两个对象的toString()相互调用，现在需要去掉一方的关联字段输出
     * java.lang.StackOverflowError
     * @return
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                ", roles=" + roles +
                '}';
    }


    // 实现UserDetails后的方法

    /**
     * 获取权限，这里使用的是GrantedAuthority类，在UserDetailsService中出现，用于组装角色权限信息
     *
     * roles: [Role{id=1, role='Cat'}, Role{id=2, role='Dog'}]
     * authorities: [ROLE_Dog, ROLE_Cat]
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<Role> roles = this.getRoles();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole()));
        }

        return authorities;
    }

    /**
     * 账户是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /**
     * 账户是否锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    /**
     * 凭证是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /**
     * 是否启用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return false;
    }
}
```

### UserDetailsService

- 接口：获取用户信息，得到UserDetails对象，一般项目要自定义类实现这个接口，从数据库中获取数据
- 实现一个方法：loadUserByUsername()根据用户名，获取用户信息（用户名称，密码，角色集合，是否可用等）
- 实现类：UserDetailsManager接口{InMemoryUserDetailsManager，JdbcUserDetailsManager）基于内存和数据库

```java
具体实现查看下一小节
```
## 数据库认证
1. SpringSecurity中有一个UserDetail接口，高度抽象用户信息类，它返回一个User类，和自定义user内容相似，包括username，password，authorities（角色、权限，继承GrantedAuthority）集合

2. 其中，角色和权限内容表达不同，角色：admin权限：ROLE_ADMIN
3. 实现接口UserDetailService接口，完成loadUserByUsername方法，返回User
4. SpringSecurity在登录时会自动调用方法，去数据库中查询出数据并验证

```java
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {    


	@Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户 " + username + " 登录失败，用户名不存在！");
        }
        // System.out.println("登录用户：" + ((Role)user.getRoles()).getRole());


        // 方式一：添加权限
        List<Role> roles = user.getRoles();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole()));
        }
        // 方法二：在自定义的User实现UserDetails后，利用上方方式实现getAuthorities()方法，直接返回
        Collection<? extends GrantedAuthority> authorities1 = user.getAuthorities();

        // 权限和角色在前缀上不同，权限会自动加上前缀ROLE_，roles()方法点进去就是GrantedAuthority
        // GrantedAuthority : ROLE_admin
        // Role : admin

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
```

```java
对于role和authorities：
roles: [Role{id=1, role='Cat'}, Role{id=2, role='Dog'}]
authorities: [ROLE_Dog, ROLE_Cat]
```
## URL权限
实现数据库认证之后，设置URL权限，就可以在网页进行权限控制

方式一：旧方式，使用默认登录页，在实现SecurityConfigurerAdapter类的cofnigure(HttpSecurity)方法中设置

```java
// 实现SecurityConfigurerAdapter类

	public void configure(HttpSecurity http){
		http.authorizeHttpRequests()
			.requestMatchers("/home").hasRole("USER")
			.requestMatchers("/home/l1/**").hasRole("Dog")
			.requestMatchers("/home/l2/**").hasRole("Cat")
			.and()
			.formLogin();
	}
	// “formLogin()”已弃用并标记为删除
```
方式二：在SecurityConfig配置类中注入配置HttpSecurity

```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth->{

                    // 设置url权限，注意所有权限的配置顺序
                    auth.requestMatchers("/home").permitAll();
                    auth.requestMatchers("/home/l0").hasRole("USER");
                    auth.requestMatchers("/home/l1/**").hasRole("Dog");
                    auth.requestMatchers("/home/l2/**").hasRole("Cat");
                    auth.anyRequest().authenticated();
                })
                .build();
    }
```
请求链接

```html
    <h2>Welcome Home</h2>
    <!--  gn cheems  -->
    <a href="/home/l0">a dog/cat</a><br>
    <a href="/home/l1">a dog</a><br>
    <a href="/home/l2">a cat</a><br>
```
Controller

```java
@RestController
public class HomeController {

    @GetMapping("/home/l0")
    public String l0(){
        return "you is a dog/cat";
    }

    @GetMapping("/home/l1")
    public String l1(){
        return "you is a dog";
    }

    @GetMapping("/home/l2")
    public String l2(){
        return "you is a cat";
    }
}
```
## 自定义登录页
查看过滤器类UsernamePasswordAuthenticationFilter，里面设置了默认的登录页的信息，只要规则匹配就会自动验证登录信息

```java
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");
    private String usernameParameter = "username";
    private String passwordParameter = "password";
    private boolean postOnly = true;
    // ...
}
```
自定义登录页的标签也要用username,password属性

```java
<!-- 这里表单发送的请求是post，在SecurityConfig.loginProcessingUrl和indexController.login自定义的登录页是get/login，表单请求可以更改名字，避免混淆-->
<form th:action="@{/login}" method="post">
    <div>
        <input type="text" name="username" placeholder="Username"/>
    </div>
    <div>
        <input type="password" name="password" placeholder="Password"/>
    </div>
    <input type="submit" value="Log in" />
</form>
```
配置security，注入HttpSecurity参数

```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth->{

                    // 设置url权限，注意所有权限的配置顺序
                    auth.requestMatchers("/home").permitAll();
                    auth.requestMatchers("/home/l0").hasRole("USER");
                    auth.requestMatchers("/home/l1/**").hasRole("Dog");
                    auth.requestMatchers("/home/l2/**").hasRole("Cat");
                    auth.anyRequest().authenticated();
                })
                .formLogin(conf->{
                    // 自定义表单登录页，这个是网页
                    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
                    conf.loginPage("/login");
                    // 表单登录请求，这个是url请求
                    conf.loginProcessingUrl("/login");
                    // 默认登录成功页
                    conf.defaultSuccessUrl("/home");
                    // 登录相关请求不需要认证
                    conf.permitAll();
                })
                .logout(conf->{
                    // 登出请求
                    conf.logoutUrl("/logout");
                    conf.logoutSuccessUrl("/login");
                    conf.permitAll();
                })
                // 使用自定义的userDetails认证过程，
                // .userDetailsService(null)
                .csrf(AbstractHttpConfigurer::disable)// 关闭跨站请求伪造保护功能
                .build();
    }
```

## AJAX登录
1. 前后端分离，使用ajax登录，传递json数据，用户发送请求，spring security接受数据并验证，然后返回json给用户
2. 还可以在security中配置成功和失败的处理器
   登录页

```html
    <script type="text/javascript" src="/js/jquery-3.7.0.min.js"></script>
    <script type="text/javascript">
        $(function (){
            $("#btnLogin").click(function () {
                console.log("ajax")
                var uname = $("#username").val();
                var pwd = $("#password").val();
                $.ajax({
                    url:"/login",
                    type:"POST",
                    data:{
                        "username":uname,
                        "password":pwd
                    },
                    dataType:"json",
                    success:function (res) {
                        alert(res.status +":"+res.msg)
                    }
                })
            })
        })
    </script>
    
    
	<div>
        使用Ajax登录，json传递数据<br>
        用户名:<input type="text" id="username"><br>
        密&nbsp;码:<input type="password" id="password"><br>
        <button id="btnLogin">登录</button><br>
    </div>
```
在security配置类中通过静态资源认证

```java
        // 静态资源
        auth.requestMatchers("/js/**").permitAll();
```

## 自定义处理器
认证处理器，自定义请求认证成功或失败后的动作

```java
/**
 * security登录认证成功处理器
 */
@Component
public class SecurityAuthSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 验证成功后执行
     * @param request 请求对象
     * @param response 响应对象
     * @param authentication security验证成功后的封装对象，包括用户的信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 登录的用户验证成功后执行
        response.setContentType("text/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.println("{\"msg\":\"登录成功！\"}");
        writer.flush();
        writer.close();
    }
}
```

```java
/**
 * security登录认证失败处理器
 */
@Component
public class SecurityAuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 登录的用户验证失败后执行
        response.setContentType("text/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.println("{\"msg\":\"登录失败(用户名或密码错误)！\"}");
        writer.flush();
        writer.close();
    }
}
```
在security配置类中通过静态资源认证

```java
         // 静态资源
        auth.requestMatchers("/js/**").permitAll();
```
注意，使用了handler处理器，就不要设置默认登录页，否则不起作用

```java
        // 使用handler类
        conf.successHandler(successHandler);
        conf.failureHandler(failureHandler);
        // 默认登录成功页，使用了handler，就不要使用默认登录页，否则handler不起作用
        // conf.defaultSuccessUrl("/home");
```
## 使用JSON格式
在用ajax的过程中使用json传递数据
创建vo类对象，传递数据

```java
@Data
public class Result {
    // 0成功 1失败
    Integer code;
    // 200 成功 500失败
    Integer status;
    // 消息
    String msg;
}
```

处理器

```java
@Component
public class SecurityAuthSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 验证成功后执行
     * @param request 请求对象
     * @param response 响应对象
     * @param authentication security验证成功后的封装对象，包括用户的信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 登录的用户验证成功后执行
        response.setContentType("text/json;charset=utf-8");

        Result result = new Result();
        result.setCode(0);
        result.setStatus(200);
        result.setMsg("登录成功");
        // 使用jsckson
        ObjectMapper mapper = new ObjectMapper();
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, result);

        outputStream.flush();
        outputStream.close();

        // PrintWriter writer = response.getWriter();
        // writer.println("{\"msg\":\"登录成功！\"}");
        // writer.flush();
        // writer.close();
    }
}
```

## 验证码
在用户名和密码下方添加验证码输入，在controller中生成验证码图片，然后响应给网页

```java
/**
 * 生成验证码响应到页面
 */
@Controller
@RequestMapping("/captcha")
public class ChptchaController {

    // 生成验证码的属性
    // 宽度
    private int width = 120;
    // 高度
    private int height = 30;
    // 内容在图片中的起始位置
    private int drawY = 20;
    // 文字的间隔
    private int space = 15;
    // 验证码文字个数
    private int charCount = 6;
    // 验证码内容数组 注意数字0和字母O容易混淆，最好注释掉
    private String chars[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P"
            ,"Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9"};

    /**
     * 绘制一个图片，将图片响应给请求
     * @param request
     * @param response
     */
    @GetMapping("/code")
    public void makeCaptchaCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 创建一个背景透明的图片，图片格式使用rgb表示颜色，画布
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取画笔
        Graphics graphics = image.getGraphics();
        // 设置画笔颜色 白色
        graphics.setColor(Color.white);
        // 把画布涂成白色 fillRect(矩形的起始x，矩形的起始y，矩形的宽度，矩形的高度)
        graphics.fillRect(0, 0, width, height);

        // 画内容
        // 创建字体
        Font font = new Font("宋体", Font.BOLD, 18);
        // 画笔设置字体和颜色
        graphics.setFont(font);
        graphics.setColor(Color.black);
        // 获取随机值
        int ran = 0;
        int len = chars.length;
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < charCount; i++){
            ran = new Random().nextInt(len);
            // 保存随机值
            stringBuffer.append(chars[ran]);
            // 设置随机颜色
            graphics.setColor(randomColor());
            // 画的内容，间隔，起始
            graphics.drawString(chars[ran], (i+1)*space, drawY);
        }
        // 绘制干扰线
        for(int i = 0; i < 4; i++){
            graphics.setColor(randomColor());
            int line[] = randomLine();
            graphics.drawLine(line[0], line[1], line[2], line[3]);
        }

        // 生成的验证码存到session
        request.getSession().setAttribute("code", stringBuffer.toString());
        System.out.println("captcha: " + stringBuffer.toString());

        // 设置响应没有缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 设置响应格式
        response.setContentType("image/png");

        // 输出图像 w(输出的图像，图像格式，输出到哪)
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "png", outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 生成随机颜色
     * @return
     */
    public Color randomColor(){
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return new Color(r, g, b);
    }

    /**
     * 生成干扰线的随机起始点
     * @return
     */
    public int[] randomLine(){
        Random random = new Random();
        int x1 = random.nextInt(width/2);
        int y1 = random.nextInt(height);
        int x2 = random.nextInt(width);
        int y2 = random.nextInt(height);
        return new int[]{x1, y1, x2, y2};
    }
}
```

通过验证

```java
                    // 验证码
                    auth.requestMatchers("/captcha/**").permitAll();
```

在前端页面添加验证码

```html
    <script type="text/javascript" src="/js/jquery-3.7.0.min.js"></script>
    <script type="text/javascript">
        $(function (){
            $("#btnLogin").click(function () {
                console.log("ajax")
                var uname = $("#username").val();
                var pwd = $("#password").val();
                // 用户输入验证码
                var textcode = $("#textcode").val();

                $.ajax({
                    url:"/login",
                    type:"POST",
                    data:{
                        "username":uname,
                        "password":pwd,
                        "code":textcode
                    },
                    dataType:"json",
                    success:function (res) {
                        alert(res.status +":"+res.msg)
                    }
                })
            })
        })

        function changeCode(){
            var url = "/captcha/code?t=" + new Date();
            $("#imageCode").attr("src", url);
        }
    </script>
    
    // ...
    
    <div>
        使用Ajax登录，json传递数据<br>
        用户名:<input type="text" id="username"><br>
        密&nbsp;码:<input type="password" id="password"><br>
        验证码:<input type="text" id="textcode">
        <img src="/captcha/code" id="imageCode"/>
        <a href="javascript:void(0)" onclick="changeCode()">重新获取</a><br>
        <button id="btnLogin">登录</button><br>
    </div>
```

## 异常

验证码异常处理，在过滤器处理验证码之前

```java
/**
 * 验证码异常处理，在过滤器处理验证码之前
 */
public class VerificationException extends AuthenticationException {

    public VerificationException(){
        super("验证码错误，请重新输入！");
    }
}
```

## 过滤器

### 概述

Security中有很多过滤器，例如表单登录验证使用的UsernamePasswordAuthenticationFilter，而验证码在表单登录验证之前使用，所以需要自定义一个过滤器，然后放入整个过滤器链中，并且在UsernamePasswordAuthenticationFilter之前



### 自定义过滤器

使用OncePerRequestFilter，一次性过滤器，出现异常调用handler处理器

```java
/**
 * 验证码过滤器，使用在UsernamePasswordAuthenticationFilter之前
 */
public class VerificationFilter extends OncePerRequestFilter {

    // 登录失败的handler，在过滤器抛出异常时使用
    private SecurityAuthFailureHandler failureHandler = new SecurityAuthFailureHandler();

    /**
     * 验证码过滤器
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 缺少登录成功，错误提示有问题！！原因是设置了defaultSuccessUrl()，同样的有failurehandler也不要设置默认的错误页

        // 验证码只在登录的过程中才使用这个过滤器
        String requestURI = request.getRequestURI();
        // 如果登录页和表单登录请求都使用/login，那么此处要判断是去登录页(GET)还是表单登录请求(POST)
        String method = request.getMethod();
        if(!"/login".equals(requestURI) || "GET".equals(method)){
            // 不是登录操作，不经过这个过滤器
            filterChain.doFilter(request, response);
        }else{
            try{
                // 验证验证码
                verificationCode(request);
                // 通过
                filterChain.doFilter(request, response);
            }catch (VerificationException e){
                // 验证出现异常时，跳转到表单登录失败的处理器SecurityAuthFailureHandler中
                // 1.在filter中添加handler属性，在这里调用
                // 2.在SecurityAuthFailureHandler中修改，添加一个vo.Result属性，然后判断是正常的handler还是第三方异常跳转过去的
                Result result = new Result();
                result.setCode(1);
                result.setStatus(501);
                result.setMsg("验证码错误，请重新输入！！");
                failureHandler.setResult(result);
                failureHandler.onAuthenticationFailure(request, response, e);
            }

        }


    }

    private void verificationCode(HttpServletRequest request) throws VerificationException {
        // 获取请求中的验证码Code
        String requestCode = request.getParameter("code");
        // 获取session中的验证码Code
        String sessionCode = "";
        HttpSession session = request.getSession();
        Object code = session.getAttribute("code");
        if(code != null){
            sessionCode = (String) code;
        }

        System.out.println("Verificate Captcha: session:" + sessionCode + " |request:" + requestCode);

        // 一次性验证码，使用后销毁
        if(!StringUtils.isEmpty(sessionCode)){
            // 能获取到session中的验证码，说明已经在页面生成了，现在就不能再用了
            session.removeAttribute("code");
        }

        // 判断验证码code是否正确
        if(StringUtils.isEmpty(requestCode) || StringUtils.isEmpty(sessionCode) || !requestCode.equals(sessionCode)){
            // 验证失败
            throw new VerificationException();
        }
    }
}
```

修改handler处理器，判断一下是否第三方调用（如验证码异常）

```java
/**
 * security登录认证失败处理器
 */
@Component
public class SecurityAuthFailureHandler implements AuthenticationFailureHandler {

    // 添加result属性，可以让第三方异常调用，展示异常信息
    private Result result;
    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * 验证失败后执行
     * @param request 请求对象
     * @param response 响应对象
     * @param exception security验证失败后的封装对象，包括用户的信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 登录的用户验证失败后执行
        response.setContentType("text/json;charset=utf-8");
        System.out.println("failure handler...");

        // 判断是否自定义的result，还是第三方异常调用的result，第三方异常调用时，result已经有值了
        if(result == null){
            Result localResult = new Result();
            localResult.setCode(1);
            localResult.setStatus(500);
            localResult.setMsg("登录失败(用户名或密码错误)!");
            result = localResult;
        }

        // 使用jsckson
        ObjectMapper mapper = new ObjectMapper();
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, result);

        outputStream.flush();
        outputStream.close();
    }
}
```

Security设置过滤器，注意用了handler处理器，就不要设置默认登录页

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {


    // 验证成功和失败处理器
    @Autowired
    SecurityAuthSuccessHandler successHandler;
    @Autowired
    SecurityAuthFailureHandler failureHandler;

    /**
     * 密码编码器
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     *
     * 之前的做法有在configure(AuthenticationManagerBuilder)中配置auth.userDetailsService(myDetailsService).passwordEncoder(bcry)
     * 在configure(HttpSecurity)中配置http.authorizeHttpRequests()认证
     * 现在同样使用HttpSecurity参数，HttpSecurity：具体的权限控制规则配置
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth->{

                    // 设置url权限，注意所有权限的配置顺序
                    auth.requestMatchers("/home").permitAll();
                    // 验证码
                    auth.requestMatchers("/captcha/**").permitAll();
                    // 静态资源
                    auth.requestMatchers("/js/**").permitAll();
                    auth.requestMatchers("/home/l0").hasRole("USER");
                    auth.requestMatchers("/home/l1/**").hasRole("Dog");
                    auth.requestMatchers("/home/l2/**").hasRole("Cat");
                    auth.anyRequest().authenticated();
                })
                .formLogin(conf->{
                    // 自定义表单登录页
                    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
                    conf.loginPage("/login");
                    // 表单登录请求
                    conf.loginProcessingUrl("/login");
                    // 登录成功处理器，取消defaultSuccessUrl默认登录成功页可以看到效果，如登录失败处理器类似
                    // conf.successHandler(authenticationSuccessHandler());
                    // 登录失败处理器，但此处不能在表单上方显示error信息
                    // conf.failureHandler(authenticationFailureHandler());
                    // 使用handler类
                    conf.successHandler(successHandler);
                    conf.failureHandler(failureHandler);
                    // 默认登录成功页，使用了handler，就不要使用默认登录页，否则handler不起作用
                    // conf.defaultSuccessUrl("/home");
                    // 登录相关请求不需要认证
                    conf.permitAll();
                })
                .logout(conf->{
                    // 登出请求
                    conf.logoutUrl("/logout");
                    conf.logoutSuccessUrl("/login");
                    conf.permitAll();
                })
                // 使用自定义过滤器，并且
                .addFilterBefore(new VerificationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 使用自定义的userDetails认证过程，
                // .userDetailsService(null)
                .csrf(AbstractHttpConfigurer::disable)// 关闭跨站请求伪造保护功能
                .build();
    }
}
```

html添加验证码

```html
<!DOCTYPE html>

<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">
<head>
    <title>Welcome</title>

    <script type="text/javascript" src="/js/jquery-3.7.0.min.js"></script>
    <script type="text/javascript">
        $(function (){
            $("#btnLogin").click(function () {
                console.log("ajax")
                alert("ajax")
                var uname = $("#username").val();
                var pwd = $("#password").val();
                // 用户输入验证码
                var textcode = $("#textcode").val();

                $.ajax({
                    url:"/login",
                    type:"POST",
                    // async: false,
                    data:{
                        "username":uname,
                        "password":pwd,
                        "code":textcode
                    },
                    dataType:"json",
                    success:function(res) {
                        console.log(res)
                        alert(res.status +":"+res.msg)
                    }
                })
            })
        })

        function changeCode(){
        	// 防止缓存
            var url = "/captcha/code?t=" + new Date();
            $("#imageCode").attr("src", url);
        }
    </script>


</head>
<body>
<h1>Welcome Log In</h1>
<div th:if="${param.error}">
    Invalid username and password.</div>
<div th:if="${param.logout}">
    You have been logged out.</div>

<!-- 这里表单发送的请求是post，在SecurityConfig.loginProcessingUrl和indexController.login自定义的登录页是get/login，表单请求可以更改名字，避免混淆-->
<form th:action="@{/login}" method="post">
    <div>
        <input type="text" name="username" placeholder="Username"/>
    </div>
    <div>
        <input type="password" name="password" placeholder="Password"/>
    </div>
    <input type="submit" value="Log in" />
</form>

<br>
    <div>
        使用Ajax登录，json传递数据<br>
        用户名:<input type="text" id="username"><br>
        密&nbsp;码:<input type="password" id="password"><br>
        验证码:<input type="text" id="textcode">
        <img src="/captcha/code" id="imageCode"/>
        <a href="javascript:void(0)" onclick="changeCode()">重新获取</a><br>
        <button id="btnLogin">登录</button><br>
    </div>
</body>
</html>
```


> 完整代码在https://github.com/say-hey/springboot-security-concise

