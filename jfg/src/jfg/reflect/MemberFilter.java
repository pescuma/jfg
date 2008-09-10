package jfg.reflect;

import java.lang.reflect.Member;

interface MemberFilter
{
	boolean accept(Member member);
}
