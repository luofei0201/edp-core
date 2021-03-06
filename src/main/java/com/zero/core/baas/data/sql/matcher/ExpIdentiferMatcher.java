package com.zero.core.baas.data.sql.matcher;

import com.zero.core.baas.data.sql.token.CharStream;
import com.zero.core.baas.data.sql.token.Token;
import com.zero.core.baas.data.sql.token.TokenKind;

public class ExpIdentiferMatcher extends TokenMatcher{
	@Override
	public Token match(CharStream stream) {
		if(stream.isEof()) return null;
		int start = stream.position();
		String wd = stream.nextWord();
		
		if(wd != null){
			return new Token(wd,TokenKind.EXP_IDENTIFIER,start, stream.position());
		}
		return null;
	}

}
