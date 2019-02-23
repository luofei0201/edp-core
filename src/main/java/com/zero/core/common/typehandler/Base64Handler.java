package com.zero.core.common.typehandler;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;


/**
 * 文件类型处理器
 * @author luofei
 */
public class Base64Handler extends BaseTypeHandler<String>{

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		 byte[] data=Base64.getDecoder().decode(parameter);
		 ByteArrayInputStream bis = new ByteArrayInputStream(data);
		 ps.setBinaryStream(i, bis, data.length);
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		 Blob blob = rs.getBlob(columnName);
		    byte[] returnValue = null;
		    if (null != blob) {
		      returnValue = blob.getBytes(1, (int) blob.length());
			    return Base64.getEncoder().encodeToString(returnValue);
		    }
		    return null;
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		 Blob blob = rs.getBlob(columnIndex);
		    byte[] returnValue = null;
		    if (null != blob) {
		      returnValue = blob.getBytes(1, (int) blob.length());
			    return Base64.getEncoder().encodeToString(returnValue);

		    }
		    return null;
	}

	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		 Blob blob = cs.getBlob(columnIndex);
		    byte[] returnValue = null;
		    if (null != blob) {
		      returnValue = blob.getBytes(1, (int) blob.length());
			    return Base64.getEncoder().encodeToString(returnValue);
		    }
		    return null;
	}

	

}
