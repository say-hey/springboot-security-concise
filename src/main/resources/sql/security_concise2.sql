/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.176.129_3306
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : 192.168.176.129:3306
 Source Schema         : security_concise

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 28/08/2023 15:13:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'Cat');
INSERT INTO `role` VALUES (2, 'Dog');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_account_non_expired` int(11) NULL DEFAULT NULL COMMENT '过期',
  `is_account_non_locked` int(11) NULL DEFAULT NULL,
  `is_credentials_non_expired` int(11) NULL DEFAULT NULL,
  `is_enabled` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'Jerry', '123456', 1, 1, 1, 1);
INSERT INTO `user` VALUES (7, 'user', '$2a$10$UlrFXmShICZ7sVQOa.I9TuoFWFBlMFSLKWeXNloucRozYyxZOHXFe', NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (9, 'admin', '$2a$10$5PHUgYh8A31715Vb0pjR3OWPiF8Y/Ns1P5BaSqGtD/YMC/EqyjCYa', NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (10, 'cheems', '$2a$10$5PHUgYh8A31715Vb0pjR3OWPiF8Y/Ns1P5BaSqGtD/YMC/EqyjCYa', NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (11, 'pop', '$2a$10$5PHUgYh8A31715Vb0pjR3OWPiF8Y/Ns1P5BaSqGtD/YMC/EqyjCYa', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NULL DEFAULT NULL,
  `r_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`u_id`) USING BTREE,
  INDEX `role_id`(`r_id`) USING BTREE,
  CONSTRAINT `role_id` FOREIGN KEY (`r_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_id` FOREIGN KEY (`u_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 2, 1);
INSERT INTO `user_role` VALUES (2, 2, 2);
INSERT INTO `user_role` VALUES (3, 7, 2);
INSERT INTO `user_role` VALUES (4, 9, 1);
INSERT INTO `user_role` VALUES (5, 10, 2);
INSERT INTO `user_role` VALUES (6, 11, 1);
INSERT INTO `user_role` VALUES (7, 9, 2);

SET FOREIGN_KEY_CHECKS = 1;
