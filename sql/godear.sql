
DROP TABLE IF EXISTS `bookmarker`;
CREATE TABLE `bookmarker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT "",
  `email` varchar(255) DEFAULT "",
  `password` varchar(255) DEFAULT "",
  `created_at` bigint DEFAULT "",
  PRIMARY KEY (`id`),
  KEY(`name`),
  KEY(`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bookmark_feed`;
CREATE TABLE `bookmark_feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bookmarker_id` int(11) DEFAULT -1,
  `feed_id` int(11) DEFAULT -1,
  `created_at` bigint DEFAULT -1,
  PRIMARY KEY (`id`),
  KEY(`bookmarker_id`),
  KEY(`feed_id`),
  KEY(`created_at`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `feed`;
CREATE TABLE `feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source_name` varchar(255) DEFAULT "",
  `title` varchar(255) DEFAULT "",
  `description` varchar(255) DEFAULT "",
  `rss` varchar(255) DEFAULT "",
  `link` varchar(255) DEFAULT "",
  `rank` int(11) DEFAULT 0,
  `created_at` bigint DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;