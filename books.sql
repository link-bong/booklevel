/*
 Navicat Premium Dump SQL

 Source Server         : bookself
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : books

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 27/03/2025 14:25:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for addbooks
-- ----------------------------
DROP TABLE IF EXISTS `addbooks`;
CREATE TABLE `addbooks`  (
  `bs_id` int NOT NULL,
  `b_id` int NOT NULL,
  PRIMARY KEY (`bs_id`, `b_id`) USING BTREE,
  INDEX `ad_b_id`(`b_id` ASC) USING BTREE,
  CONSTRAINT `ad_b_id` FOREIGN KEY (`b_id`) REFERENCES `books` (`b_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ad_bs_id` FOREIGN KEY (`bs_id`) REFERENCES `bookshelf` (`bs_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of addbooks
-- ----------------------------
INSERT INTO `addbooks` VALUES (1, 1);
INSERT INTO `addbooks` VALUES (3, 1);
INSERT INTO `addbooks` VALUES (5, 1);
INSERT INTO `addbooks` VALUES (1, 2);
INSERT INTO `addbooks` VALUES (2, 2);
INSERT INTO `addbooks` VALUES (4, 2);
INSERT INTO `addbooks` VALUES (1, 3);
INSERT INTO `addbooks` VALUES (2, 3);
INSERT INTO `addbooks` VALUES (3, 3);
INSERT INTO `addbooks` VALUES (5, 3);
INSERT INTO `addbooks` VALUES (1, 4);
INSERT INTO `addbooks` VALUES (2, 4);
INSERT INTO `addbooks` VALUES (4, 4);
INSERT INTO `addbooks` VALUES (1, 5);
INSERT INTO `addbooks` VALUES (3, 5);
INSERT INTO `addbooks` VALUES (5, 5);
INSERT INTO `addbooks` VALUES (2, 6);
INSERT INTO `addbooks` VALUES (4, 6);
INSERT INTO `addbooks` VALUES (3, 7);
INSERT INTO `addbooks` VALUES (5, 7);
INSERT INTO `addbooks` VALUES (4, 8);
INSERT INTO `addbooks` VALUES (3, 9);
INSERT INTO `addbooks` VALUES (5, 10);

-- ----------------------------
-- Table structure for bookmark
-- ----------------------------
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE `bookmark`  (
  `cp_id` int NOT NULL,
  `r_id` int NOT NULL,
  `bm_time` datetime NOT NULL,
  PRIMARY KEY (`cp_id`, `r_id`) USING BTREE,
  INDEX `bookmark_ibfk_1`(`r_id` ASC) USING BTREE,
  CONSTRAINT `bookmark_ibfk_1` FOREIGN KEY (`r_id`) REFERENCES `reader` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cp_id` FOREIGN KEY (`cp_id`) REFERENCES `chapter` (`cp_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bookmark
-- ----------------------------
INSERT INTO `bookmark` VALUES (2, 3, '2023-01-06 11:00:00');
INSERT INTO `bookmark` VALUES (3, 1, '2023-01-02 15:00:00');
INSERT INTO `bookmark` VALUES (4, 4, '2023-01-07 13:00:00');
INSERT INTO `bookmark` VALUES (5, 2, '2023-01-03 12:00:00');
INSERT INTO `bookmark` VALUES (6, 4, '2023-01-08 17:00:00');
INSERT INTO `bookmark` VALUES (7, 2, '2023-01-04 16:00:00');
INSERT INTO `bookmark` VALUES (8, 5, '2023-01-09 10:30:00');
INSERT INTO `bookmark` VALUES (9, 3, '2023-01-05 14:00:00');
INSERT INTO `bookmark` VALUES (10, 5, '2023-01-10 15:30:00');

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `b_id` int NOT NULL,
  `b_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `b_state` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `b_category` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `w_id` int NOT NULL,
  PRIMARY KEY (`b_id`) USING BTREE,
  INDEX `w_id`(`w_id` ASC) USING BTREE,
  CONSTRAINT `w_id` FOREIGN KEY (`w_id`) REFERENCES `writer` (`w_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (1, '蛙', '已售罄', '小说', 1);
INSERT INTO `books` VALUES (2, '活着', '已出版', '经典文学', 2);
INSERT INTO `books` VALUES (3, '平凡的世界', '已出版', '小说', 3);
INSERT INTO `books` VALUES (4, '围城', '已出版', '小说', 4);
INSERT INTO `books` VALUES (5, '边城', '已出版', '小说', 5);
INSERT INTO `books` VALUES (6, '丰乳肥臀', '已出版', '小说', 1);
INSERT INTO `books` VALUES (7, '许三观卖血记', '已出版', '小说', 2);
INSERT INTO `books` VALUES (8, '人生', '已出版', '小说', 3);
INSERT INTO `books` VALUES (9, '管锥编', '学术著作', '文学研究', 4);
INSERT INTO `books` VALUES (10, '长河', '已出版', '小说', 5);

-- ----------------------------
-- Table structure for bookshelf
-- ----------------------------
DROP TABLE IF EXISTS `bookshelf`;
CREATE TABLE `bookshelf`  (
  `bs_id` int NOT NULL,
  `num` int NOT NULL,
  `r_id` int NOT NULL,
  PRIMARY KEY (`bs_id`) USING BTREE,
  INDEX `r_id`(`r_id` ASC) USING BTREE,
  CONSTRAINT `r_id` FOREIGN KEY (`r_id`) REFERENCES `reader` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bookshelf
-- ----------------------------
INSERT INTO `bookshelf` VALUES (1, 5, 1);
INSERT INTO `bookshelf` VALUES (2, 4, 2);
INSERT INTO `bookshelf` VALUES (3, 6, 3);
INSERT INTO `bookshelf` VALUES (4, 3, 4);
INSERT INTO `bookshelf` VALUES (5, 7, 5);
INSERT INTO `bookshelf` VALUES (6, 2, 1);
INSERT INTO `bookshelf` VALUES (7, 3, 2);
INSERT INTO `bookshelf` VALUES (8, 4, 3);
INSERT INTO `bookshelf` VALUES (9, 5, 4);
INSERT INTO `bookshelf` VALUES (10, 6, 5);

-- ----------------------------
-- Table structure for browse
-- ----------------------------
DROP TABLE IF EXISTS `browse`;
CREATE TABLE `browse`  (
  `browse_id` int NOT NULL AUTO_INCREMENT,
  `r_id` int NOT NULL,
  `b_id` int NOT NULL,
  `browsetime` datetime NOT NULL,
  PRIMARY KEY (`browse_id`) USING BTREE,
  UNIQUE INDEX `unique_browse`(`r_id` ASC, `b_id` ASC) USING BTREE,
  INDEX `browse_b_id`(`b_id` ASC) USING BTREE,
  CONSTRAINT `browse_b_id` FOREIGN KEY (`b_id`) REFERENCES `books` (`b_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `browse_r_id` FOREIGN KEY (`r_id`) REFERENCES `reader` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of browse
-- ----------------------------
INSERT INTO `browse` VALUES (1, 1, 1, '2023-03-01 10:00:00');
INSERT INTO `browse` VALUES (2, 1, 2, '2023-03-02 11:00:00');
INSERT INTO `browse` VALUES (3, 1, 3, '2023-03-03 12:00:00');
INSERT INTO `browse` VALUES (4, 1, 4, '2023-03-04 13:00:00');
INSERT INTO `browse` VALUES (5, 1, 5, '2023-03-05 14:00:00');
INSERT INTO `browse` VALUES (6, 2, 2, '2023-03-06 15:00:00');
INSERT INTO `browse` VALUES (7, 2, 3, '2023-03-07 16:00:00');
INSERT INTO `browse` VALUES (8, 2, 4, '2023-03-08 17:00:00');
INSERT INTO `browse` VALUES (9, 2, 6, '2023-03-09 18:00:00');
INSERT INTO `browse` VALUES (10, 3, 1, '2023-03-10 09:00:00');
INSERT INTO `browse` VALUES (11, 3, 3, '2023-03-11 10:00:00');
INSERT INTO `browse` VALUES (12, 3, 5, '2023-03-12 11:00:00');
INSERT INTO `browse` VALUES (13, 3, 7, '2023-03-13 12:00:00');
INSERT INTO `browse` VALUES (14, 4, 2, '2023-03-14 13:00:00');
INSERT INTO `browse` VALUES (15, 4, 4, '2023-03-15 14:00:00');
INSERT INTO `browse` VALUES (16, 4, 6, '2023-03-16 15:00:00');
INSERT INTO `browse` VALUES (17, 4, 8, '2023-03-17 16:00:00');
INSERT INTO `browse` VALUES (18, 5, 1, '2023-03-18 17:00:00');
INSERT INTO `browse` VALUES (19, 5, 3, '2023-03-19 18:00:00');
INSERT INTO `browse` VALUES (20, 5, 5, '2023-03-20 09:00:00');
INSERT INTO `browse` VALUES (21, 5, 7, '2023-03-21 10:00:00');

-- ----------------------------
-- Table structure for chapter
-- ----------------------------
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter`  (
  `cp_id` int NOT NULL,
  `cp_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `b_id` int NOT NULL,
  PRIMARY KEY (`cp_id`) USING BTREE,
  INDEX `b_id`(`b_id` ASC) USING BTREE,
  CONSTRAINT `b_id` FOREIGN KEY (`b_id`) REFERENCES `books` (`b_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chapter
-- ----------------------------
INSERT INTO `chapter` VALUES (1, '第一章：蛙的诞生', 1);
INSERT INTO `chapter` VALUES (2, '第二章：命运的转折', 1);
INSERT INTO `chapter` VALUES (3, '经典文学 - 经典文学 - 经典文学 - 经典文学 - 经典文学 - 第一章：福贵的早年', 2);
INSERT INTO `chapter` VALUES (4, '经典文学 - 经典文学 - 经典文学 - 经典文学 - 经典文学 - 第二章：苦难的开始', 2);
INSERT INTO `chapter` VALUES (5, '第一章：孙少平的出场', 3);
INSERT INTO `chapter` VALUES (6, '第二章：爱情的萌芽', 3);
INSERT INTO `chapter` VALUES (7, '第一章：方鸿渐的归来', 4);
INSERT INTO `chapter` VALUES (8, '第二章：围城中的生活', 4);
INSERT INTO `chapter` VALUES (9, '第一章：翠翠的成长', 5);
INSERT INTO `chapter` VALUES (10, '第二章：爱情的波折', 5);
INSERT INTO `chapter` VALUES (11, '第三章：蛙的抗争', 1);
INSERT INTO `chapter` VALUES (12, '经典文学 - 经典文学 - 经典文学 - 经典文学 - 经典文学 - 第三章：福贵的挣扎', 2);
INSERT INTO `chapter` VALUES (13, '第三章：孙少平的奋斗', 3);
INSERT INTO `chapter` VALUES (14, '第三章：方鸿渐的困境', 4);
INSERT INTO `chapter` VALUES (15, '第三章：翠翠的坚守', 5);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `cm_id` int NOT NULL,
  `cm_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cm_time` datetime NOT NULL,
  `b_id` int NOT NULL,
  `r_id` int NOT NULL,
  PRIMARY KEY (`cm_id`) USING BTREE,
  INDEX `cm_b_id`(`b_id` ASC) USING BTREE,
  INDEX `cm_r_id`(`r_id` ASC) USING BTREE,
  CONSTRAINT `cm_b_id` FOREIGN KEY (`b_id`) REFERENCES `books` (`b_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cm_r_id` FOREIGN KEY (`r_id`) REFERENCES `reader` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, '更新后的评论内容', '2023-02-01 09:00:00', 1, 1);
INSERT INTO `comment` VALUES (2, '情节跌宕起伏，非常吸引人。', '2023-02-02 10:00:00', 2, 1);
INSERT INTO `comment` VALUES (3, '人物形象鲜明，让人印象深刻。', '2023-02-03 11:00:00', 3, 2);
INSERT INTO `comment` VALUES (4, '文字优美，富有感染力。', '2023-02-04 12:00:00', 4, 2);
INSERT INTO `comment` VALUES (5, '故事很感人，让人深思。', '2023-02-05 13:00:00', 5, 3);
INSERT INTO `comment` VALUES (6, '对人性的描写很细腻。', '2023-02-06 14:00:00', 1, 3);
INSERT INTO `comment` VALUES (7, '充满了生活的智慧。', '2023-02-07 15:00:00', 2, 4);
INSERT INTO `comment` VALUES (8, '展现了时代的画卷。', '2023-02-08 16:00:00', 3, 4);
INSERT INTO `comment` VALUES (9, '结构严谨，逻辑清晰。', '2023-02-09 17:00:00', 4, 5);
INSERT INTO `comment` VALUES (10, '是一部经典之作。', '2023-02-10 18:00:00', 5, 5);
INSERT INTO `comment` VALUES (11, '很有深度的作品。', '2023-02-11 09:30:00', 6, 1);
INSERT INTO `comment` VALUES (12, '值得一读再读。', '2023-02-12 10:30:00', 7, 2);
INSERT INTO `comment` VALUES (13, '触动人心的故事。', '2023-02-13 11:30:00', 8, 3);
INSERT INTO `comment` VALUES (14, '精彩绝伦的描写。', '2023-02-14 12:30:00', 9, 4);

-- ----------------------------
-- Table structure for reader
-- ----------------------------
DROP TABLE IF EXISTS `reader`;
CREATE TABLE `reader`  (
  `r_id` int NOT NULL AUTO_INCREMENT,
  `r_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `r_password` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`r_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reader
-- ----------------------------
INSERT INTO `reader` VALUES (1, '新用户名', '123456');
INSERT INTO `reader` VALUES (2, '李四', 'lisi456');
INSERT INTO `reader` VALUES (3, '王五', 'wangwu789');
INSERT INTO `reader` VALUES (4, '赵六', 'zhaoliu001');
INSERT INTO `reader` VALUES (5, '孙七', 'sunqi002');
INSERT INTO `reader` VALUES (6, '周八', 'zhouba003');
INSERT INTO `reader` VALUES (7, '吴九', 'wujiu004');
INSERT INTO `reader` VALUES (8, '郑十', 'zhengshi005');
INSERT INTO `reader` VALUES (9, '钱十一', 'qianshiyi006');
INSERT INTO `reader` VALUES (10, '陈十二', 'chenshieryi007');
INSERT INTO `reader` VALUES (11, 'test_user', 'test_password');
INSERT INTO `reader` VALUES (12, 'wee', '666');

-- ----------------------------
-- Table structure for reader_reply
-- ----------------------------
DROP TABLE IF EXISTS `reader_reply`;
CREATE TABLE `reader_reply`  (
  `rr_id` int NOT NULL AUTO_INCREMENT,
  `cm_id` int NOT NULL,
  `r_id` int NOT NULL,
  `rr_time` datetime NOT NULL,
  `rr_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`rr_id`) USING BTREE,
  UNIQUE INDEX `unique_reply`(`cm_id` ASC, `r_id` ASC) USING BTREE,
  INDEX `rr_r_id`(`r_id` ASC) USING BTREE,
  CONSTRAINT `rr_cm_id` FOREIGN KEY (`cm_id`) REFERENCES `comment` (`cm_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rr_r_id` FOREIGN KEY (`r_id`) REFERENCES `reader` (`r_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reader_reply
-- ----------------------------
INSERT INTO `reader_reply` VALUES (1, 1, 2, '2023-02-03 09:00:00', '我也有同感，这本书真的很棒！');
INSERT INTO `reader_reply` VALUES (2, 2, 3, '2023-02-04 10:00:00', '情节确实很吸引人，让人欲罢不能。');
INSERT INTO `reader_reply` VALUES (3, 3, 4, '2023-02-05 11:00:00', '人物形象确实很鲜明，作者刻画得很细腻。');
INSERT INTO `reader_reply` VALUES (4, 4, 5, '2023-02-06 12:00:00', '文字优美是这本书的一大亮点。');
INSERT INTO `reader_reply` VALUES (5, 5, 1, '2023-02-07 13:00:00', '故事确实很感人，让人回味无穷。');
INSERT INTO `reader_reply` VALUES (6, 6, 3, '2023-02-08 14:00:00', '对人性的描写很深刻，值得细细品味。');
INSERT INTO `reader_reply` VALUES (7, 7, 4, '2023-02-09 15:00:00', '书中充满了生活的智慧，让人受益匪浅。');
INSERT INTO `reader_reply` VALUES (8, 8, 5, '2023-02-10 16:00:00', '展现的时代画卷很真实，仿佛置身其中。');
INSERT INTO `reader_reply` VALUES (9, 9, 1, '2023-02-11 17:00:00', '结构严谨使得故事更加精彩。');
INSERT INTO `reader_reply` VALUES (10, 10, 2, '2023-02-12 18:00:00', '确实是一部经典之作，值得推荐给更多人。');
INSERT INTO `reader_reply` VALUES (11, 11, 3, '2023-02-13 09:30:00', '很有深度，让人对很多问题有了新的思考。');
INSERT INTO `reader_reply` VALUES (12, 12, 4, '2023-02-14 10:30:00', '值得反复阅读，每次都有新的收获。');
INSERT INTO `reader_reply` VALUES (13, 13, 5, '2023-02-15 11:30:00', '触动人心的故事，让人感同身受。');
INSERT INTO `reader_reply` VALUES (14, 14, 1, '2023-02-16 12:30:00', '精彩的描写让人身临其境。');

-- ----------------------------
-- Table structure for wirter_reply
-- ----------------------------
DROP TABLE IF EXISTS `wirter_reply`;
CREATE TABLE `wirter_reply`  (
  `wr_id` int NOT NULL AUTO_INCREMENT,
  `w_id` int NOT NULL,
  `cm_id` int NOT NULL,
  `wr_time` datetime NOT NULL,
  `wr_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`wr_id`) USING BTREE,
  UNIQUE INDEX `unique_writer_reply`(`w_id` ASC, `cm_id` ASC) USING BTREE,
  INDEX `wr_cm_id`(`cm_id` ASC) USING BTREE,
  CONSTRAINT `wr_cm_id` FOREIGN KEY (`cm_id`) REFERENCES `comment` (`cm_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `wr_w_id` FOREIGN KEY (`w_id`) REFERENCES `writer` (`w_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wirter_reply
-- ----------------------------
INSERT INTO `wirter_reply` VALUES (1, 1, 1, '2023-02-02 11:00:00', '感谢你的认可，我会继续努力创作。');
INSERT INTO `wirter_reply` VALUES (2, 1, 6, '2023-02-07 16:00:00', '谢谢你对人性描写的肯定，我会深入挖掘人性。');
INSERT INTO `wirter_reply` VALUES (3, 1, 11, '2023-02-12 09:30:00', '感谢你的评价，我会继续创作有深度的作品。');
INSERT INTO `wirter_reply` VALUES (4, 2, 2, '2023-02-03 12:00:00', '很高兴你喜欢这本书，希望它能给你带来更多思考。');
INSERT INTO `wirter_reply` VALUES (5, 2, 7, '2023-02-08 17:00:00', '感谢你发现了书中的生活智慧，希望对你有所帮助。');
INSERT INTO `wirter_reply` VALUES (6, 2, 12, '2023-02-13 10:30:00', '很高兴你觉得值得一读再读，我会不断提升作品质量。');
INSERT INTO `wirter_reply` VALUES (7, 3, 3, '2023-02-04 13:00:00', '感谢你的评价，我会不断塑造更生动的人物形象。');
INSERT INTO `wirter_reply` VALUES (8, 3, 8, '2023-02-09 18:00:00', '很高兴你能从书中看到时代画卷，我会继续努力展现时代。');
INSERT INTO `wirter_reply` VALUES (9, 3, 13, '2023-02-14 11:30:00', '感谢你的喜欢，我会创作更多触动人心的故事。');
INSERT INTO `wirter_reply` VALUES (10, 4, 4, '2023-02-05 14:00:00', '你的夸奖是我前进的动力，我会保持文字的优美。');
INSERT INTO `wirter_reply` VALUES (11, 4, 9, '2023-02-10 19:00:00', '感谢你对结构的认可，我会注重作品的逻辑性。');
INSERT INTO `wirter_reply` VALUES (12, 4, 14, '2023-02-15 12:30:00', '谢谢你对描写的赞赏，我会继续努力提高写作水平。');
INSERT INTO `wirter_reply` VALUES (13, 5, 5, '2023-02-06 15:00:00', '很开心我的故事能感动你，我会继续创作好作品。');
INSERT INTO `wirter_reply` VALUES (14, 5, 10, '2023-02-11 20:00:00', '很荣幸你认为这是经典之作，我会不辜负读者期望。');

-- ----------------------------
-- Table structure for writer
-- ----------------------------
DROP TABLE IF EXISTS `writer`;
CREATE TABLE `writer`  (
  `w_id` int NOT NULL,
  `w_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `w_password` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`w_id` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of writer
-- ----------------------------
INSERT INTO `writer` VALUES (5, '沈从文', 'shencongwen002');
INSERT INTO `writer` VALUES (4, '钱钟书', 'qianshuzhong001');
INSERT INTO `writer` VALUES (3, '路遥', 'luyao789');
INSERT INTO `writer` VALUES (2, '余华', 'yuhua456');
INSERT INTO `writer` VALUES (1, '莫言', 'newpassword123');

-- ----------------------------
-- View structure for author_book_status
-- ----------------------------
DROP VIEW IF EXISTS `author_book_status`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `author_book_status` AS select `w`.`w_name` AS `w_name`,`b`.`b_name` AS `b_name`,`b`.`b_state` AS `b_state` from (`writer` `w` join `books` `b` on((`w`.`w_id` = `b`.`w_id`)));

-- ----------------------------
-- View structure for reader_comment_view
-- ----------------------------
DROP VIEW IF EXISTS `reader_comment_view`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `reader_comment_view` AS select `r`.`r_name` AS `r_name`,`c`.`cm_content` AS `cm_content`,`c`.`cm_time` AS `cm_time` from (`reader` `r` join `comment` `c` on((`r`.`r_id` = `c`.`r_id`)));

-- ----------------------------
-- Triggers structure for table books
-- ----------------------------
DROP TRIGGER IF EXISTS `update_chapter_status`;
delimiter ;;
CREATE TRIGGER `update_chapter_status` AFTER UPDATE ON `books` FOR EACH ROW BEGIN
    IF NEW.b_state = '已下架' THEN
        UPDATE chapter SET chapter_status = '无效' WHERE b_id = NEW.b_id;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table comment
-- ----------------------------
DROP TRIGGER IF EXISTS `update_comment_count`;
delimiter ;;
CREATE TRIGGER `update_comment_count` AFTER INSERT ON `comment` FOR EACH ROW BEGIN
    UPDATE books SET comment_count = comment_count + 1 WHERE b_id = NEW.b_id;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table reader
-- ----------------------------
DROP TRIGGER IF EXISTS `delete_reader_related_records`;
delimiter ;;
CREATE TRIGGER `delete_reader_related_records` BEFORE DELETE ON `reader` FOR EACH ROW BEGIN
    DELETE FROM browse WHERE r_id = OLD.r_id;
    DELETE FROM bookmark WHERE r_id = OLD.r_id;
    DELETE FROM comment WHERE r_id = OLD.r_id;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
