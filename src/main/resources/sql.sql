CREATE TABLE IF NOT EXISTS `portal_acc_online`
(
    `id`           int AUTO_INCREMENT
    CONSTRAINT `PRIMARY`
    PRIMARY KEY,
    `ip`           varchar(255)                        NULL,
    `mac`          varchar(255)                        NULL,
    `nas_port`     int                                 NULL,
    `session_id`   varchar(255)                        NULL,
    `username`     varchar(255)                        NULL,
    `start_source` int                                 NULL,
    `created`      timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    `last_update`  timestamp DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    `start_time`   mediumtext                          NULL,
    `end_time`     mediumtext                          NULL,
    `nas_ip`       varchar(255)                        NULL
    )
    AUTO_INCREMENT = 0;

CREATE INDEX `portal_acc_online_id_index`
    ON `portal_acc_online` (`id`);

CREATE INDEX `portal_acc_online_ip_index`
    ON `portal_acc_online` (`ip`);

CREATE INDEX `portal_acc_online_mac_index`
    ON `portal_acc_online` (`mac`);

CREATE INDEX `portal_acc_online_session_id_index`
    ON `portal_acc_online` (`session_id`);

CREATE INDEX `portal_acc_online_username_index`
    ON `portal_acc_online` (`username`);

CREATE TABLE IF NOT EXISTS `portal_act`
(
    `id`            int AUTO_INCREMENT
    CONSTRAINT `PRIMARY`
    PRIMARY KEY,
    `username`      varchar(255)                        NULL,
    `customer_name` varchar(255)                        NULL,
    `uuid`          varchar(255)                        NULL,
    `created`       timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    `last_update`   timestamp DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
    );

CREATE INDEX `portal_act_id_index`
    ON `portal_act` (`id`);

CREATE INDEX `portal_act_username_index`
    ON `portal_act` (`username`);

CREATE INDEX `portal_act_uuid_index`
    ON `portal_act` (`uuid`);

CREATE TABLE IF NOT EXISTS `portal_act_uuid`
(
    `id`          int AUTO_INCREMENT
    CONSTRAINT `PRIMARY`
    PRIMARY KEY,
    `uuid`        varchar(255)                        NULL,
    `status`      varchar(255)                        NULL,
    `created`     timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    `last_update` timestamp DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
    )
    AUTO_INCREMENT = 0;

CREATE INDEX `portal_act_uuid__index`
    ON `portal_act_uuid` (`status`);

CREATE INDEX `portal_act_uuid_id_index`
    ON `portal_act_uuid` (`id`);

CREATE INDEX `portal_act_uuid_uuid_index`
    ON `portal_act_uuid` (`uuid`);

CREATE TABLE IF NOT EXISTS `portal_authentication`
(
    `id`          int AUTO_INCREMENT
    CONSTRAINT `PRIMARY`
    PRIMARY KEY,
    `ip`          varchar(255)                        NULL,
    `mac`         varchar(255)                        NULL,
    `created`     timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    `type`        varchar(255)                        NULL,
    `status`      char                                NULL,
    `last_update` timestamp DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    `session_id`  varchar(255)                        NULL
    )
    AUTO_INCREMENT = 0;

CREATE INDEX `portal_authentication_id_index`
    ON `portal_authentication` (`id`);

CREATE INDEX `portal_authentication_ip_index`
    ON `portal_authentication` (`ip`);

CREATE INDEX `portal_authentication_mac_index`
    ON `portal_authentication` (`mac`);

CREATE INDEX `portal_authentication_status_index`
    ON `portal_authentication` (`status`);

CREATE TABLE IF NOT EXISTS `portal_ssid`
(
    `id`          int AUTO_INCREMENT
    CONSTRAINT `PRIMARY`
    PRIMARY KEY,
    `ssid`        varchar(255)                        NULL,
    `created`     timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    `last_update` timestamp DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
    );

CREATE INDEX `portal_ssid_ssid_index`
    ON `portal_ssid` (`ssid`);

