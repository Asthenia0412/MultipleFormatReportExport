package io.github.asthenia0412.multipleformatreportexport.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis JSON类型处理器
 * 用于处理数据库中的JSON字段与Java对象之间的转换
 */
@MappedTypes({String[].class})
public class JsonTypeHandler extends BaseTypeHandler<String[]> {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) 
            throws SQLException {
        try {
            ps.setString(i, OBJECT_MAPPER.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting String[] to JSON", e);
        }
    }
    
    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }
    
    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }
    
    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }
    
    private String[] parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new String[0];
        }
        
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<String[]>() {});
        } catch (JsonProcessingException e) {
            // 如果解析失败，返回空数组
            return new String[0];
        }
    }
}
