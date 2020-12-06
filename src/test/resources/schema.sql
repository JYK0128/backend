CREATE TABLE member (
    id          NUMBER GENERATED BY DEFAULT AS IDENTITY,
    email       NVARCHAR2(255) NOT NULL,
    provider    NVARCHAR2(255) NOT NULL,
    PRIMARY KEY (id)
);

create table post
(
    id          NUMBER GENERATED BY DEFAULT AS IDENTITY,
    tag         NVARCHAR2(255),
    title       NVARCHAR2(255),
    contents     NVARCHAR2(255),
    updated     DATE DEFAULT CURRENT_DATE,
    views       NUMBER,
    writer_id   NUMBER,
    PRIMARY KEY (id),
    FOREIGN KEY (writer_id) REFERENCES member (id) ON DELETE CASCADE
);

create table message
(
    id          NUMBER GENERATED BY DEFAULT AS IDENTITY,
    message     NVARCHAR2(255),
    post_id     NUMBER,
    writer_id   NUMBER,
    topic_id    NUMBER,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE SET NULL,
    FOREIGN KEY (writer_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES message (id) ON DELETE SET NULL
);

create table upload
(
    id          NUMBER GENERATED BY DEFAULT AS IDENTITY,
    filename    NVARCHAR2(255),
    uuid        NVARCHAR2(255),
    post_id     NUMBER,
    primary key (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);
