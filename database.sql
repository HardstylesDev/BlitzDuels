CREATE TABLE IF NOT EXISTS `data` (
  `kills` int(11) NOT NULL,
  `deaths` int(11) DEFAULT 0,
  `wins` int(11) DEFAULT NULL,
  `streak` int(11) DEFAULT 0,
  `elo` int(11) DEFAULT NULL,
  `layout` varchar(9999) DEFAULT NULL,
  `coins` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
