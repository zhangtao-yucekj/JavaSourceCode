package com.lagou.utils;


/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * @author Clinton Begin
 */
public class GenericTokenParser {

    private final String openToken;
    private final String closeToken;
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text) {
        StringBuilder builder = new StringBuilder();
        StringBuilder expression = new StringBuilder();
        if (text != null && text.length() > 0) {
            char[] src = text.toCharArray();
            int offset = 0;

            for(int start = text.indexOf(this.openToken, offset); start > -1; start = text.indexOf(this.openToken, offset)) {
                if (start > 0 && src[start - 1] == '\\') {
                    builder.append(src, offset, start - offset - 1).append(this.openToken);
                    offset = start + this.openToken.length();
                } else {
                    expression.setLength(0);
                    builder.append(src, offset, start - offset);
                    offset = start + this.openToken.length();

                    int end;
                    for(end = text.indexOf(this.closeToken, offset); end > -1; end = text.indexOf(this.closeToken, offset)) {
                        if (end <= offset || src[end - 1] != '\\') {
                            expression.append(src, offset, end - offset);
                            int var10000 = end + this.closeToken.length();
                            break;
                        }

                        expression.append(src, offset, end - offset - 1).append(this.closeToken);
                        offset = end + this.closeToken.length();
                    }

                    if (end == -1) {
                        builder.append(src, start, src.length - start);
                        offset = src.length;
                    } else {
                        builder.append(this.handler.handleToken(expression.toString()));
                        offset = end + this.closeToken.length();
                    }
                }
            }

            if (offset < src.length) {
                builder.append(src, offset, src.length - offset);
            }
        }

        return builder.toString();
    }
}


