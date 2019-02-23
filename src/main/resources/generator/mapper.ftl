package ${package};

import ${tableClass.fullClassName};
import org.springframework.stereotype.Repository;


/**
* 通用 Mapper 代码生成器
*
* @author mapper-generator
*/
@Repository("${tableClass.variableName?cap_first}Dao")
public interface ${tableClass.shortClassName}${mapperSuffix} extends ${baseMapper!"com.zero.core.mybatis.mapper.common.CommMapper"}<${tableClass.variableName?cap_first}> {

}




