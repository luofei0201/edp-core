package com.zero.core.baas.data.sql.matcher;

import com.zero.core.baas.data.sql.token.CharStream;
import com.zero.core.baas.data.sql.token.Token;

public abstract class TokenMatcher {

	public abstract Token match(CharStream stream);

}
