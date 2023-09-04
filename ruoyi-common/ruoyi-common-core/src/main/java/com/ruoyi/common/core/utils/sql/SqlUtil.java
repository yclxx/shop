package com.ruoyi.common.core.utils.sql;

import com.ruoyi.common.core.exception.UtilException;
import com.ruoyi.common.core.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * sql操作工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlUtil {

    /**
     * 定义常用的 sql关键字
     */
    public static final String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        if (!isValidOrderBySql(value) || sqlValidate(value) || value.length() > 50) {
            throw new UtilException("参数不符合规范，不能进行查询");
        }
        return value;
    }

    /**
     * @description 匹配效验
     */
    protected static boolean sqlValidate(String str) {
        // 统一转为小写
        String s = str.toLowerCase();
        // 过滤掉的sql关键字，特殊字符前面需要加\\进行转义
        String badStr = "select|update|delete|insert|truncate|substr|ascii|declare|exec|master|into|drop|execute|table|declare|sitename|xp_cmdshell|like|from|grant|group_concat|column_name|information_schema.columns|table_schema|union|where|case|when|then|current|database";
        String[] split = badStr.split("\\|");
        for (String s1 : split) {
            if (s.contains(s1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字检查
     */
    public static void filterKeyword(String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
        for (String sqlKeyword : sqlKeywords) {
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) {
                throw new UtilException("参数存在SQL注入风险");
            }
        }
    }
}
