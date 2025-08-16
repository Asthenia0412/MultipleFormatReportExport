package io.github.asthenia0412.multipleformatreportexport.mapper;

import io.github.asthenia0412.multipleformatreportexport.entity.FormatInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 格式信息数据访问层
 */
@Mapper
public interface FormatInfoMapper {
    
    /**
     * 根据格式名称查询格式信息
     * @param formatName 格式名称
     * @return 格式信息
     */
    FormatInfo selectByFormatName(@Param("formatName") String formatName);
    
    /**
     * 查询所有支持的格式
     * @return 支持的格式列表
     */
    List<FormatInfo> selectSupportedFormats();
    
    /**
     * 查询所有格式信息
     * @return 所有格式信息列表
     */
    List<FormatInfo> selectAll();
    
    /**
     * 插入格式信息
     * @param formatInfo 格式信息
     * @return 影响行数
     */
    int insert(FormatInfo formatInfo);
    
    /**
     * 更新格式信息
     * @param formatInfo 格式信息
     * @return 影响行数
     */
    int update(FormatInfo formatInfo);
    
    /**
     * 删除格式信息
     * @param formatName 格式名称
     * @return 影响行数
     */
    int deleteByFormatName(@Param("formatName") String formatName);
    
    /**
     * 检查格式是否支持
     * @param formatName 格式名称
     * @return 是否支持
     */
    boolean isFormatSupported(@Param("formatName") String formatName);
}
