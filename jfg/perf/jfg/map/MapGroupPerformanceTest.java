package jfg.map;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

public class MapGroupPerformanceTest extends JapexDriverBase
{
	private Map<String, Object> map;
	
	@Override
	public void prepare(TestCase testCase)
	{
		map = new LinkedHashMap<String, Object>();
		map.put("abdDe", "aaa");
		map.put("number", Integer.valueOf(1));
		map.put("chk", Boolean.TRUE);
		map.put("byz_asdf", null);
	}
	
	@Override
	public void run(TestCase testCase)
	{
		MapGroup group = new MapGroup(map);
		group.getAttributes();
	}
}
