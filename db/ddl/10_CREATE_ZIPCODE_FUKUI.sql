CREATE TABLE PUBLIC.ZIPCODE_FUKUI (
  ZENKOKU_CHIHOU_KOUKYOU_DANTAI_CODE CHAR(5) NOT NULL,
  OLD_ZIPCODE VARCHAR(5),
  ZIPCODE CHAR(7) NOT NULL,
  TODOUFUKEN_KANA VARCHAR(50) NOT NULL,
  SHIKUCHOUSON_KANA VARCHAR(50) NOT NULL,
  CHOUIKI_KANA VARCHAR(100),
  TODOUFUKEN VARCHAR(50),
  SHIKUCHOUSON VARCHAR(50),
  CHOUIKI VARCHAR(100),
  FLAG01 CHAR(1),
  FLAG02 CHAR(1),
  FLAG03 CHAR(1),
  FLAG04 CHAR(1),
  FLAG05 CHAR(1),
  FLAG06 CHAR(1)
);