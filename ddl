CREATE TABLE students(
    id INT AUTO_INCREMENT PRIMARY KEY,
    studentId VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    grade INT NOT NULL,
    className VARCHAR(50) NOT NULL,
    registerYear INT NOT NULL,
    totalCoupon INT,
    totalPoint BIGINT,
    createdDate DATETIME NOT NULL,
    updatedDate DATETIME
);

CREATE TABLE coupons(
    couponId INT AUTO_INCREMENT PRIMARY KEY,
    studentId VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    createdDate DATETIME NOT NULL,

    FOREIGN KEY (studentId) REFERENCES students(studentId) ON DELETE CASCADE
);

CREATE TABLE couponPurchase(
    purchaseId INT AUTO_INCREMENT PRIMARY KEY,
    price INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    createdDate DATETIME NOT NULL,
    updatedDate DATETIME NULL,
    couponId INT NOT NULL,
    studentId INT NOT NULL,

    FOREIGN KEY (couponId) REFERENCES coupons(couponId) ON DELETE CASCADE,
    FOREIGN KEY (studentId) REFERENCES coupons(studentId) ON DELETE CASCADE
);

CREATE TABLE getPoints(
    getPointId INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    content VARCHAR(300),
    point INT,
    createdDate DATETIME NOT NULL,

    FOREIGN KEY (studentId) REFERENCES students(studentId) ON DELETE CASCADE
);

CREATE TABLE news(
    newsId INT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(300),
    createdDate DATETIME NOT NULL
);

CREATE TABLE orders(
    orderId INT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(300),
    price INT,
    amount INT,
    state VARCHAR(100),
    createdDate DATETIME NOT NULL,
    updatedDate DATETIME,
    deletedDate DATETIME,
    studentId INT NOT NULL,
    stockId INT NOT NULL,
    FOREIGN KEY (studentId) REFERENCES students(studentId) ON DELETE CASCADE,
    FOREIGN KEY (stockId) REFERENCES stocks(stockId) ON DELETE CASCADE
);

CREATE TABLE stocks(
    stockId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    content VARCHAR(100),
    publicationBalance INT,
    publicationPrice INT NOT NULL,
    prevPrice INT NOT NULL,
    createdDate DATETIME NOT NULL
);

CREATE TABLE transactions(
    transactionId INT AUTO_INCREMENT PRIMARY KEY,
    createdDate DATETIME NOT NULL,
    sellOrderId INT,
    buyOrderId INT,
    FOREIGN KEY (sellOrderId) REFERENCES stocks(stockId) ON DELETE CASCADE,
    FOREIGN KEY (buyOrderId) REFERENCES stocks(stockId) ON DELETE CASCADE
);