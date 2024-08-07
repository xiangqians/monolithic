package org.xiangqian.monolithic.common.generator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MySQL 生成器
 *
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
public class MysqlGenerator extends AbsGenerator {

    /**
     * MySQL 8.0 关键字和保留字
     * Keywords and Reserved Words
     * https://dev.mysql.com/doc/refman/8.0/en/keywords.html
     */
    private static final Set<String> keywordsAndReservedWords = Set.of(
            // A
            "ACCESSIBLE", //  (R)
            "ACCOUNT",
            "ACTION",
            "ACTIVE", // added in 8.0.14 (nonreserved)
            "ADD", //  (R)
            "ADMIN", // became nonreserved in 8.0.12
            "AFTER",
            "AGAINST",
            "AGGREGATE",
            "ALGORITHM",
            "ALL", //  (R)
            "ALTER", //  (R)
            "ALWAYS",
            "ANALYSE", // removed in 8.0.1
            "ANALYZE", //  (R)
            "AND", //  (R)
            "ANY",
            "ARRAY", // added in 8.0.17 (reserved); became nonreserved in 8.0.19
            "AS", //  (R)
            "ASC", //  (R)
            "ASCII",
            "ASENSITIVE", //  (R)
            "AT",
            "ATTRIBUTE", // added in 8.0.21 (nonreserved)
            "AUTHENTICATION", // added in 8.0.27 (nonreserved)
            "AUTOEXTEND_SIZE",
            "AUTO_INCREMENT",
            "AVG",
            "AVG_ROW_LENGTH",

            // B
            "BACKUP",
            "BEFORE", //  (R)
            "BEGIN",
            "BETWEEN", //  (R)
            "BIGINT", //  (R)
            "BINARY", //  (R)
            "BINLOG",
            "BIT",
            "BLOB", //  (R)
            "BLOCK",
            "BOOL",
            "BOOLEAN",
            "BOTH", //  (R)
            "BTREE",
            "BUCKETS", // added in 8.0.2 (nonreserved)
            "BULK", // added in 8.0.32 (nonreserved)
            "BY", //  (R)
            "BYTE",

            // C
            "CACHE",
            "CALL", //  (R)
            "CASCADE", //  (R)
            "CASCADED",
            "CASE", //  (R)
            "CATALOG_NAME",
            "CHAIN",
            "CHALLENGE_RESPONSE", // added in 8.0.27 (nonreserved)
            "CHANGE", //  (R)
            "CHANGED",
            "CHANNEL",
            "CHAR", //  (R)
            "CHARACTER", //  (R)
            "CHARSET",
            "CHECK", //  (R)
            "CHECKSUM",
            "CIPHER",
            "CLASS_ORIGIN",
            "CLIENT",
            "CLONE", // added in 8.0.3 (nonreserved)
            "CLOSE",
            "COALESCE",
            "CODE",
            "COLLATE", //  (R)
            "COLLATION",
            "COLUMN", //  (R)
            "COLUMNS",
            "COLUMN_FORMAT",
            "COLUMN_NAME",
            "COMMENT",
            "COMMIT",
            "COMMITTED",
            "COMPACT",
            "COMPLETION",
            "COMPONENT",
            "COMPRESSED",
            "COMPRESSION",
            "CONCURRENT",
            "CONDITION", //  (R)
            "CONNECTION",
            "CONSISTENT",
            "CONSTRAINT", //  (R)
            "CONSTRAINT_CATALOG",
            "CONSTRAINT_NAME",
            "CONSTRAINT_SCHEMA",
            "CONTAINS",
            "CONTEXT",
            "CONTINUE", //  (R)
            "CONVERT", //  (R)
            "CPU",
            "CREATE", //  (R)
            "CROSS", //  (R)
            "CUBE", //  (R); became reserved in 8.0.1
            "CUME_DIST", //  (R); added in 8.0.2 (reserved)
            "CURRENT",
            "CURRENT_DATE", //  (R)
            "CURRENT_TIME", //  (R)
            "CURRENT_TIMESTAMP", //  (R)
            "CURRENT_USER", //  (R)
            "CURSOR", //  (R)
            "CURSOR_NAME",

            // D
            "DATA",
            "DATABASE", //  (R)
            "DATABASES", //  (R)
            "DATAFILE",
            "DATE",
            "DATETIME",
            "DAY",
            "DAY_HOUR", //  (R)
            "DAY_MICROSECOND", //  (R)
            "DAY_MINUTE", //  (R)
            "DAY_SECOND", //  (R)
            "DEALLOCATE",
            "DEC", //  (R)
            "DECIMAL", //  (R)
            "DECLARE", //  (R)
            "DEFAULT", //  (R)
            "DEFAULT_AUTH",
            "DEFINER",
            "DEFINITION", // added in 8.0.4 (nonreserved)
            "DELAYED", //  (R)
            "DELAY_KEY_WRITE",
            "DELETE", //  (R)
            "DENSE_RANK", //  (R); added in 8.0.2 (reserved)
            "DESC", //  (R)
            "DESCRIBE", //  (R)
            "DESCRIPTION", // added in 8.0.4 (nonreserved)
            "DES_KEY_FILE", // removed in 8.0.3
            "DETERMINISTIC", //  (R)
            "DIAGNOSTICS",
            "DIRECTORY",
            "DISABLE",
            "DISCARD",
            "DISK",
            "DISTINCT", //  (R)
            "DISTINCTROW", //  (R)
            "DIV", //  (R)
            "DO",
            "DOUBLE", //  (R)
            "DROP", //  (R)
            "DUAL", //  (R)
            "DUMPFILE",
            "DUPLICATE",
            "DYNAMIC",

            // E
            "EACH", //  (R)
            "ELSE", //  (R)
            "ELSEIF", //  (R)
            "EMPTY", //  (R); added in 8.0.4 (reserved)
            "ENABLE",
            "ENCLOSED", //  (R)
            "ENCRYPTION",
            "END",
            "ENDS",
            "ENFORCED", // added in 8.0.16 (nonreserved)
            "ENGINE",
            "ENGINES",
            "ENGINE_ATTRIBUTE", // added in 8.0.21 (nonreserved)
            "ENUM",
            "ERROR",
            "ERRORS",
            "ESCAPE",
            "ESCAPED", //  (R)
            "EVENT",
            "EVENTS",
            "EVERY",
            "EXCEPT", //  (R)
            "EXCHANGE",
            "EXCLUDE", // added in 8.0.2 (nonreserved)
            "EXECUTE",
            "EXISTS", //  (R)
            "EXIT", //  (R)
            "EXPANSION",
            "EXPIRE",
            "EXPLAIN", //  (R)
            "EXPORT",
            "EXTENDED",
            "EXTENT_SIZE",

            // F
            "FACTOR", // added in 8.0.27 (nonreserved)
            "FAILED_LOGIN_ATTEMPTS", // added in 8.0.19 (nonreserved)
            "FALSE", //  (R)
            "FAST",
            "FAULTS",
            "FETCH", //  (R)
            "FIELDS",
            "FILE",
            "FILE_BLOCK_SIZE",
            "FILTER",
            "FINISH", // added in 8.0.27 (nonreserved)
            "FIRST",
            "FIRST_VALUE", //  (R); added in 8.0.2 (reserved)
            "FIXED",
            "FLOAT", //  (R)
            "FLOAT4", //  (R)
            "FLOAT8", //  (R)
            "FLUSH",
            "FOLLOWING", // added in 8.0.2 (nonreserved)
            "FOLLOWS",
            "FOR", //  (R)
            "FORCE", //  (R)
            "FOREIGN", //  (R)
            "FORMAT",
            "FOUND",
            "FROM", //  (R)
            "FULL",
            "FULLTEXT", //  (R)
            "FUNCTION", //  (R); became reserved in 8.0.1

            // G
            "GENERAL",
            "GENERATE", // added in 8.0.32 (nonreserved)
            "GENERATED", //  (R)
            "GEOMCOLLECTION", // added in 8.0.11 (nonreserved)
            "GEOMETRY",
            "GEOMETRYCOLLECTION",
            "GET", //  (R)
            "GET_FORMAT",
            "GET_MASTER_PUBLIC_KEY", // added in 8.0.4 (reserved); became nonreserved in 8.0.11
            "GET_SOURCE_PUBLIC_KEY", // added in 8.0.23 (nonreserved)
            "GLOBAL",
            "GRANT", //  (R)
            "GRANTS",
            "GROUP", //  (R)
            "GROUPING", //  (R); added in 8.0.1 (reserved)
            "GROUPS", //  (R); added in 8.0.2 (reserved)
            "GROUP_REPLICATION",
            "GTID_ONLY", // added in 8.0.27 (nonreserved)

            // H
            "HANDLER",
            "HASH",
            "HAVING", //  (R)
            "HELP",
            "HIGH_PRIORITY", //  (R)
            "HISTOGRAM", // added in 8.0.2 (nonreserved)
            "HISTORY", // added in 8.0.3 (nonreserved)
            "HOST",
            "HOSTS",
            "HOUR",
            "HOUR_MICROSECOND", //  (R)
            "HOUR_MINUTE", //  (R)
            "HOUR_SECOND", //  (R)

            // I
            "IDENTIFIED",
            "IF", //  (R)
            "IGNORE", //  (R)
            "IGNORE_SERVER_IDS",
            "IMPORT",
            "IN", //  (R)
            "INACTIVE", // added in 8.0.14 (nonreserved)
            "INDEX", //  (R)
            "INDEXES",
            "INFILE", //  (R)
            "INITIAL", // added in 8.0.27 (nonreserved)
            "INITIAL_SIZE",
            "INITIATE", // added in 8.0.27 (nonreserved)
            "INNER", //  (R)
            "INOUT", //  (R)
            "INSENSITIVE", //  (R)
            "INSERT", //  (R)
            "INSERT_METHOD",
            "INSTALL",
            "INSTANCE",
            "INT", //  (R)
            "INT1", //  (R)
            "INT2", //  (R)
            "INT3", //  (R)
            "INT4", //  (R)
            "INT8", //  (R)
            "INTEGER", //  (R)
            "INTERSECT", //  (R); added in 8.0.31 (reserved)
            "INTERVAL", //  (R)
            "INTO", //  (R)
            "INVISIBLE",
            "INVOKER",
            "IO",
            "IO_AFTER_GTIDS", //  (R)
            "IO_BEFORE_GTIDS", //  (R)
            "IO_THREAD",
            "IPC",
            "IS", //  (R)
            "ISOLATION",
            "ISSUER",
            "ITERATE", //  (R)

            // J
            "JOIN", //  (R)
            "JSON",
            "JSON_TABLE", //  (R); added in 8.0.4 (reserved)
            "JSON_VALUE", // added in 8.0.21 (nonreserved)

            // K
            "KEY", //  (R)
            "KEYRING", // added in 8.0.24 (nonreserved)
            "KEYS", //  (R)
            "KEY_BLOCK_SIZE",
            "KILL", //  (R)

            // L
            "LAG", //  (R); added in 8.0.2 (reserved)
            "LANGUAGE",
            "LAST",
            "LAST_VALUE", //  (R); added in 8.0.2 (reserved)
            "LATERAL", //  (R); added in 8.0.14 (reserved)
            "LEAD", //  (R); added in 8.0.2 (reserved)
            "LEADING", //  (R)
            "LEAVE", //  (R)
            "LEAVES",
            "LEFT", //  (R)
            "LESS",
            "LEVEL",
            "LIKE", //  (R)
            "LIMIT", //  (R)
            "LINEAR", //  (R)
            "LINES", //  (R)
            "LINESTRING",
            "LIST",
            "LOAD", //  (R)
            "LOCAL",
            "LOCALTIME", //  (R)
            "LOCALTIMESTAMP", //  (R)
            "LOCK", //  (R)
            "LOCKED", // added in 8.0.1 (nonreserved)
            "LOCKS",
            "LOGFILE",
            "LOGS",
            "LONG", //  (R)
            "LONGBLOB", //  (R)
            "LONGTEXT", //  (R)
            "LOOP", //  (R)
            "LOW_PRIORITY", //  (R)

            // M
            "MASTER",
            "MASTER_AUTO_POSITION",
            "MASTER_BIND", //  (R)
            "MASTER_COMPRESSION_ALGORITHMS", // added in 8.0.18 (nonreserved)
            "MASTER_CONNECT_RETRY",
            "MASTER_DELAY",
            "MASTER_HEARTBEAT_PERIOD",
            "MASTER_HOST",
            "MASTER_LOG_FILE",
            "MASTER_LOG_POS",
            "MASTER_PASSWORD",
            "MASTER_PORT",
            "MASTER_PUBLIC_KEY_PATH", // added in 8.0.4 (nonreserved)
            "MASTER_RETRY_COUNT",
            "MASTER_SERVER_ID", // removed in 8.0.23
            "MASTER_SSL",
            "MASTER_SSL_CA",
            "MASTER_SSL_CAPATH",
            "MASTER_SSL_CERT",
            "MASTER_SSL_CIPHER",
            "MASTER_SSL_CRL",
            "MASTER_SSL_CRLPATH",
            "MASTER_SSL_KEY",
            "MASTER_SSL_VERIFY_SERVER_CERT", //  (R)
            "MASTER_TLS_CIPHERSUITES", // added in 8.0.19 (nonreserved)
            "MASTER_TLS_VERSION",
            "MASTER_USER",
            "MASTER_ZSTD_COMPRESSION_LEVEL", // added in 8.0.18 (nonreserved)
            "MATCH", //  (R)
            "MAXVALUE", //  (R)
            "MAX_CONNECTIONS_PER_HOUR",
            "MAX_QUERIES_PER_HOUR",
            "MAX_ROWS",
            "MAX_SIZE",
            "MAX_UPDATES_PER_HOUR",
            "MAX_USER_CONNECTIONS",
            "MEDIUM",
            "MEDIUMBLOB", //  (R)
            "MEDIUMINT", //  (R)
            "MEDIUMTEXT", //  (R)
            "MEMBER", // added in 8.0.17 (reserved); became nonreserved in 8.0.19
            "MEMORY",
            "MERGE",
            "MESSAGE_TEXT",
            "MICROSECOND",
            "MIDDLEINT", //  (R)
            "MIGRATE",
            "MINUTE",
            "MINUTE_MICROSECOND", //  (R)
            "MINUTE_SECOND", //  (R)
            "MIN_ROWS",
            "MOD", //  (R)
            "MODE",
            "MODIFIES", //  (R)
            "MODIFY",
            "MONTH",
            "MULTILINESTRING",
            "MULTIPOINT",
            "MULTIPOLYGON",
            "MUTEX",
            "MYSQL_ERRNO",

            // N
            "NAME",
            "NAMES",
            "NATIONAL",
            "NATURAL", //  (R)
            "NCHAR",
            "NDB",
            "NDBCLUSTER",
            "NESTED", // added in 8.0.4 (nonreserved)
            "NETWORK_NAMESPACE", // added in 8.0.16 (nonreserved)
            "NEVER",
            "NEW",
            "NEXT",
            "NO",
            "NODEGROUP",
            "NONE",
            "NOT", //  (R)
            "NOWAIT", // added in 8.0.1 (nonreserved)
            "NO_WAIT",
            "NO_WRITE_TO_BINLOG", //  (R)
            "NTH_VALUE", //  (R); added in 8.0.2 (reserved)
            "NTILE", //  (R); added in 8.0.2 (reserved)
            "NULL", //  (R)
            "NULLS", // added in 8.0.2 (nonreserved)
            "NUMBER",
            "NUMERIC", //  (R)
            "NVARCHAR",

            // O
            "OF", //  (R); added in 8.0.1 (reserved)
            "OFF", // added in 8.0.20 (nonreserved)
            "OFFSET",
            "OJ", // added in 8.0.16 (nonreserved)
            "OLD", // added in 8.0.14 (nonreserved)
            "ON", //  (R)
            "ONE",
            "ONLY",
            "OPEN",
            "OPTIMIZE", //  (R)
            "OPTIMIZER_COSTS", //  (R)
            "OPTION", //  (R)
            "OPTIONAL", // added in 8.0.13 (nonreserved)
            "OPTIONALLY", //  (R)
            "OPTIONS",
            "OR", //  (R)
            "ORDER", //  (R)
            "ORDINALITY", // added in 8.0.4 (nonreserved)
            "ORGANIZATION", // added in 8.0.4 (nonreserved)
            "OTHERS", // added in 8.0.2 (nonreserved)
            "OUT", //  (R)
            "OUTER", //  (R)
            "OUTFILE", //  (R)
            "OVER", //  (R); added in 8.0.2 (reserved)
            "OWNER",

            // P
            "PACK_KEYS",
            "PAGE",
            "PARSER",
            "PARTIAL",
            "PARTITION", //  (R)
            "PARTITIONING",
            "PARTITIONS",
            "PASSWORD",
            "PASSWORD_LOCK_TIME", // added in 8.0.19 (nonreserved)
            "PATH", // added in 8.0.4 (nonreserved)
            "PERCENT_RANK", //  (R); added in 8.0.2 (reserved)
            "PERSIST", // became nonreserved in 8.0.16
            "PERSIST_ONLY", // added in 8.0.2 (reserved); became nonreserved in 8.0.16
            "PHASE",
            "PLUGIN",
            "PLUGINS",
            "PLUGIN_DIR",
            "POINT",
            "POLYGON",
            "PORT",
            "PRECEDES",
            "PRECEDING", // added in 8.0.2 (nonreserved)
            "PRECISION", //  (R)
            "PREPARE",
            "PRESERVE",
            "PREV",
            "PRIMARY", //  (R)
            "PRIVILEGES",
            "PRIVILEGE_CHECKS_USER", // added in 8.0.18 (nonreserved)
            "PROCEDURE", //  (R)
            "PROCESS", // added in 8.0.11 (nonreserved)
            "PROCESSLIST",
            "PROFILE",
            "PROFILES",
            "PROXY",
            "PURGE", //  (R)

            // Q
            "QUARTER",
            "QUERY",
            "QUICK",

            // R
            "RANDOM", // added in 8.0.18 (nonreserved)
            "RANGE", //  (R)
            "RANK", //  (R); added in 8.0.2 (reserved)
            "READ", //  (R)
            "READS", //  (R)
            "READ_ONLY",
            "READ_WRITE", //  (R)
            "REAL", //  (R)
            "REBUILD",
            "RECOVER",
            "RECURSIVE", //  (R); added in 8.0.1 (reserved)
            "REDOFILE", // removed in 8.0.3
            "REDO_BUFFER_SIZE",
            "REDUNDANT",
            "REFERENCE", // added in 8.0.4 (nonreserved)
            "REFERENCES", //  (R)
            "REGEXP", //  (R)
            "REGISTRATION", // added in 8.0.27 (nonreserved)
            "RELAY",
            "RELAYLOG",
            "RELAY_LOG_FILE",
            "RELAY_LOG_POS",
            "RELAY_THREAD",
            "RELEASE", //  (R)
            "RELOAD",
            "REMOTE", // added in 8.0.3 (nonreserved); removed in 8.0.14
            "REMOVE",
            "RENAME", //  (R)
            "REORGANIZE",
            "REPAIR",
            "REPEAT", //  (R)
            "REPEATABLE",
            "REPLACE", //  (R)
            "REPLICA", // added in 8.0.22 (nonreserved)
            "REPLICAS", // added in 8.0.22 (nonreserved)
            "REPLICATE_DO_DB",
            "REPLICATE_DO_TABLE",
            "REPLICATE_IGNORE_DB",
            "REPLICATE_IGNORE_TABLE",
            "REPLICATE_REWRITE_DB",
            "REPLICATE_WILD_DO_TABLE",
            "REPLICATE_WILD_IGNORE_TABLE",
            "REPLICATION",
            "REQUIRE", //  (R)
            "REQUIRE_ROW_FORMAT", // added in 8.0.19 (nonreserved)
            "RESET",
            "RESIGNAL", //  (R)
            "RESOURCE", // added in 8.0.3 (nonreserved)
            "RESPECT", // added in 8.0.2 (nonreserved)
            "RESTART", // added in 8.0.4 (nonreserved)
            "RESTORE",
            "RESTRICT", //  (R)
            "RESUME",
            "RETAIN", // added in 8.0.14 (nonreserved)
            "RETURN", //  (R)
            "RETURNED_SQLSTATE",
            "RETURNING", // added in 8.0.21 (nonreserved)
            "RETURNS",
            "REUSE", // added in 8.0.3 (nonreserved)
            "REVERSE",
            "REVOKE", //  (R)
            "RIGHT", //  (R)
            "RLIKE", //  (R)
            "ROLE", // became nonreserved in 8.0.1
            "ROLLBACK",
            "ROLLUP",
            "ROTATE",
            "ROUTINE",
            "ROW", //  (R); became reserved in 8.0.2
            "ROWS", //  (R); became reserved in 8.0.2
            "ROW_COUNT",
            "ROW_FORMAT",
            "ROW_NUMBER", //  (R); added in 8.0.2 (reserved)
            "RTREE",

            // S
            "SAVEPOINT",
            "SCHEDULE",
            "SCHEMA", //  (R)
            "SCHEMAS", //  (R)
            "SCHEMA_NAME",
            "SECOND",
            "SECONDARY", // added in 8.0.16 (nonreserved)
            "SECONDARY_ENGINE", // added in 8.0.13 (nonreserved)
            "SECONDARY_ENGINE_ATTRIBUTE", // added in 8.0.21 (nonreserved)
            "SECONDARY_LOAD", // added in 8.0.13 (nonreserved)
            "SECONDARY_UNLOAD", // added in 8.0.13 (nonreserved)
            "SECOND_MICROSECOND", //  (R)
            "SECURITY",
            "SELECT", //  (R)
            "SENSITIVE", //  (R)
            "SEPARATOR", //  (R)
            "SERIAL",
            "SERIALIZABLE",
            "SERVER",
            "SESSION",
            "SET", //  (R)
            "SHARE",
            "SHOW", //  (R)
            "SHUTDOWN",
            "SIGNAL", //  (R)
            "SIGNED",
            "SIMPLE",
            "SKIP", // added in 8.0.1 (nonreserved)
            "SLAVE",
            "SLOW",
            "SMALLINT", //  (R)
            "SNAPSHOT",
            "SOCKET",
            "SOME",
            "SONAME",
            "SOUNDS",
            "SOURCE",
            "SOURCE_AUTO_POSITION", // added in 8.0.23 (nonreserved)
            "SOURCE_BIND", // added in 8.0.23 (nonreserved)
            "SOURCE_COMPRESSION_ALGORITHMS", // added in 8.0.23 (nonreserved)
            "SOURCE_CONNECT_RETRY", // added in 8.0.23 (nonreserved)
            "SOURCE_DELAY", // added in 8.0.23 (nonreserved)
            "SOURCE_HEARTBEAT_PERIOD", // added in 8.0.23 (nonreserved)
            "SOURCE_HOST", // added in 8.0.23 (nonreserved)
            "SOURCE_LOG_FILE", // added in 8.0.23 (nonreserved)
            "SOURCE_LOG_POS", // added in 8.0.23 (nonreserved)
            "SOURCE_PASSWORD", // added in 8.0.23 (nonreserved)
            "SOURCE_PORT", // added in 8.0.23 (nonreserved)
            "SOURCE_PUBLIC_KEY_PATH", // added in 8.0.23 (nonreserved)
            "SOURCE_RETRY_COUNT", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_CA", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_CAPATH", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_CERT", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_CIPHER", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_CRL", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_CRLPATH", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_KEY", // added in 8.0.23 (nonreserved)
            "SOURCE_SSL_VERIFY_SERVER_CERT", // added in 8.0.23 (nonreserved)
            "SOURCE_TLS_CIPHERSUITES", // added in 8.0.23 (nonreserved)
            "SOURCE_TLS_VERSION", // added in 8.0.23 (nonreserved)
            "SOURCE_USER", // added in 8.0.23 (nonreserved)
            "SOURCE_ZSTD_COMPRESSION_LEVEL", // added in 8.0.23 (nonreserved)
            "SPATIAL", //  (R)
            "SPECIFIC", //  (R)
            "SQL", //  (R)
            "SQLEXCEPTION", //  (R)
            "SQLSTATE", //  (R)
            "SQLWARNING", //  (R)
            "SQL_AFTER_GTIDS",
            "SQL_AFTER_MTS_GAPS",
            "SQL_BEFORE_GTIDS",
            "SQL_BIG_RESULT", //  (R)
            "SQL_BUFFER_RESULT",
            "SQL_CACHE", // removed in 8.0.3
            "SQL_CALC_FOUND_ROWS", //  (R)
            "SQL_NO_CACHE",
            "SQL_SMALL_RESULT", //  (R)
            "SQL_THREAD",
            "SQL_TSI_DAY",
            "SQL_TSI_HOUR",
            "SQL_TSI_MINUTE",
            "SQL_TSI_MONTH",
            "SQL_TSI_QUARTER",
            "SQL_TSI_SECOND",
            "SQL_TSI_WEEK",
            "SQL_TSI_YEAR",
            "SRID", // added in 8.0.3 (nonreserved)
            "SSL", //  (R)
            "STACKED",
            "START",
            "STARTING", //  (R)
            "STARTS",
            "STATS_AUTO_RECALC",
            "STATS_PERSISTENT",
            "STATS_SAMPLE_PAGES",
            "STATUS",
            "STOP",
            "STORAGE",
            "STORED", //  (R)
            "STRAIGHT_JOIN", //  (R)
            "STREAM", // added in 8.0.20 (nonreserved)
            "STRING",
            "SUBCLASS_ORIGIN",
            "SUBJECT",
            "SUBPARTITION",
            "SUBPARTITIONS",
            "SUPER",
            "SUSPEND",
            "SWAPS",
            "SWITCHES",
            "SYSTEM", //  (R); added in 8.0.3 (reserved)

            // T
            "TABLE", //  (R)
            "TABLES",
            "TABLESPACE",
            "TABLE_CHECKSUM",
            "TABLE_NAME",
            "TEMPORARY",
            "TEMPTABLE",
            "TERMINATED", //  (R)
            "TEXT",
            "THAN",
            "THEN", //  (R)
            "THREAD_PRIORITY", // added in 8.0.3 (nonreserved)
            "TIES", // added in 8.0.2 (nonreserved)
            "TIME",
            "TIMESTAMP",
            "TIMESTAMPADD",
            "TIMESTAMPDIFF",
            "TINYBLOB", //  (R)
            "TINYINT", //  (R)
            "TINYTEXT", //  (R)
            "TLS", // added in 8.0.21 (nonreserved)
            "TO", //  (R)
            "TRAILING", //  (R)
            "TRANSACTION",
            "TRIGGER", //  (R)
            "TRIGGERS",
            "TRUE", //  (R)
            "TRUNCATE",
            "TYPE",
            "TYPES",

            // U
            "UNBOUNDED", // added in 8.0.2 (nonreserved)
            "UNCOMMITTED",
            "UNDEFINED",
            "UNDO", //  (R)
            "UNDOFILE",
            "UNDO_BUFFER_SIZE",
            "UNICODE",
            "UNINSTALL",
            "UNION", //  (R)
            "UNIQUE", //  (R)
            "UNKNOWN",
            "UNLOCK", //  (R)
            "UNREGISTER", // added in 8.0.27 (nonreserved)
            "UNSIGNED", //  (R)
            "UNTIL",
            "UPDATE", //  (R)
            "UPGRADE",
            "URL", // added in 8.0.32 (nonreserved)
            "USAGE", //  (R)
            "USE", //  (R)
            "USER",
            "USER_RESOURCES",
            "USE_FRM",
            "USING", //  (R)
            "UTC_DATE", //  (R)
            "UTC_TIME", //  (R)
            "UTC_TIMESTAMP", //  (R)

            // V
            "VALIDATION",
            "VALUE",
            "VALUES", //  (R)
            "VARBINARY", //  (R)
            "VARCHAR", //  (R)
            "VARCHARACTER", //  (R)
            "VARIABLES",
            "VARYING", //  (R)
            "VCPU", // added in 8.0.3 (nonreserved)
            "VIEW",
            "VIRTUAL", //  (R)
            "VISIBLE",

            // W
            "WAIT",
            "WARNINGS",
            "WEEK",
            "WEIGHT_STRING",
            "WHEN", //  (R)
            "WHERE", //  (R)
            "WHILE", //  (R)
            "WINDOW", //  (R); added in 8.0.2 (reserved)
            "WITH", //  (R)
            "WITHOUT",
            "WORK",
            "WRAPPER",
            "WRITE", //  (R)

            // X
            "X509",
            "XA",
            "XID",
            "XML",
            "XOR", //  (R)

            // Y
            "YEAR",
            "YEAR_MONTH", //  (R)

            // Z
            "ZEROFILL", //  (R)
            "ZONE" // added in 8.0.22 (nonreserved)
    );

    /**
     * @param host   数据库主机
     * @param port   数据库端口
     * @param user   数据库用户名
     * @param passwd 数据库密码
     */
    public MysqlGenerator(String host, Integer port, String user, String passwd) throws ClassNotFoundException {
        super(String.format("jdbc:mysql://%s:%s/information_schema?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true", host, port), user, passwd);

        // 加载数据库驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    @Override
    protected String getSql(String database, String[] tables) {
        return String.format("SELECT t.TABLE_NAME, t.TABLE_COMMENT, c.COLUMN_NAME, c.COLUMN_TYPE, c.COLUMN_COMMENT" +
                        " FROM information_schema.`TABLES` t" +
                        " JOIN information_schema.`COLUMNS` c ON c.TABLE_SCHEMA = t.TABLE_SCHEMA AND c.TABLE_NAME = t.TABLE_NAME" +
                        " WHERE t.TABLE_SCHEMA = '%s' AND t.TABLE_NAME IN (%s)" +
                        " ORDER BY c.TABLE_NAME, c.ORDINAL_POSITION",
                database, Arrays.stream(tables).map(table -> String.format("'%s'", table)).collect(Collectors.joining(", ")));
    }

    @Override
    protected String getEscapeTableName(String tableName) {
        if (keywordsAndReservedWords.contains(tableName.toUpperCase())) {
            tableName = String.format("`%s`", tableName);
        }
        return tableName;
    }

    @Override
    protected String getEscapeColumnName(String columnName) {
        if (keywordsAndReservedWords.contains(columnName.toUpperCase())) {
            columnName = String.format("`%s`", columnName);
        }
        return columnName;
    }

    @Override
    protected Class<?> getColumnType(String columnType) {
        // TINYINT
        // 1 个字节
        // 有符号范围：-128 到 127
        // 无符号范围：0 到 255
        if (isMatchWords(columnType, "TINYINT")) {
            return Byte.class;
        }

        // SMALLINT
        // 2 个字节
        // 有符号范围：-32768 到 32767
        // 无符号范围：0 到 65535
        if (isMatchWords(columnType, "SMALLINT")) {
            return Short.class;
        }

        // INT 或 INTEGER
        // 4 个字节
        // 有符号范围：-2147483648 到 2147483647
        // 无符号范围：0 到 4294967295
        if (isMatchAnyWords(columnType, "INT", "INTEGER")) {
            if (isMatchWords(columnType, "UNSIGNED")) {
                return Long.class;
            }
            return Integer.class;
        }

        // BIGINT
        // 8 个字节
        // 有符号范围：-9223372036854775808 到 9223372036854775807
        // 无符号范围：0 到 18446744073709551615
        if (isMatchWords(columnType, "BIGINT")) {
            return Long.class;
        }

        // VARCHAR
        if (isMatchWords(columnType, "VARCHAR")) {
            return String.class;
        }

        // TEXT
        // 最大存储容量为 65535 个字节或字符（约 64 KB）
        if (isMatchWords(columnType, "TEXT")) {
            return String.class;
        }

        // MEDIUMTEXT
        // 最大存储容量为 16777215 个字节或字符（约 16 MB）
        if (isMatchWords(columnType, "MEDIUMTEXT")) {
            return String.class;
        }

        // LONGTEXT
        // 最大存储容量为 4294967295 个字节或字符（约 4 GB）
        if (isMatchWords(columnType, "LONGTEXT")) {
            return String.class;
        }

        // DATETIME
        if (isMatchWords(columnType, "DATETIME")) {
            return LocalDateTime.class;
        }

        System.err.println(columnType);
        return null;
    }

}
