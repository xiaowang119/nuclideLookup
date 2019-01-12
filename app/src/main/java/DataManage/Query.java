package DataManage;

import java.util.Vector;

public class Query {

	private Vector<StructDecay> datasourceDecays;   //需要查找的数据源
	
	public Query(Vector<StructDecay> decays)
	{
		datasourceDecays = decays;
	}
	/*
	 * 函数功能：  通过核素查询，核素的符号和核素的质量数
	 */
	public Vector<StructDecay> findByNuclide(int queryType, String str) throws Exception
	{
		Vector<StructDecay> resultDecays = new Vector<StructDecay>();
		switch(queryType)
		{
			case QueryTypeByNuclide.NAME_PARENT:
				resultDecays = findByParentName(str);
				break;
			case QueryTypeByNuclide.MASS_PARENT:
				resultDecays = findByParentMass(str);
				break;
			case QueryTypeByNuclide.NAME_SON:
				resultDecays = findBySonName(str);
				break;
			case QueryTypeByNuclide.MASS_SON:
				resultDecays = findBySonMass(str);
		}
		return resultDecays;
	}

	/*
	 * 函数功能：   通过母核的元素符号查询
	 */
	private Vector<StructDecay> findByParentName (String nuclidename) throws Exception
	{
		Vector<StructDecay> resultdecays = new Vector<StructDecay>();
		for(int i = 0; i < datasourceDecays.size(); i++)
		{
			if(datasourceDecays.elementAt(i).getParentName().equals(nuclidename))
				resultdecays.add(datasourceDecays.elementAt(i));
		}
		return resultdecays;
	}
	/*
	 * 函数功能：   通过母核的质量数查询
	 */
	private Vector<StructDecay> findByParentMass(String nuclideMass)
	{
		Vector<StructDecay> resultdecays = new Vector<StructDecay>();
		for(int i = 0;i<datasourceDecays.size();i++)
		{
			if(datasourceDecays.elementAt(i).getParentMass().equals(nuclideMass))
				resultdecays.add(datasourceDecays.elementAt(i));
		}
		return resultdecays;
	}
	/*
	 * 函数功能：   通过子核的元素符号查询
	 */
	private Vector<StructDecay> findBySonName(String nuclidename)
	{
		Vector<StructDecay> resultdecays = new Vector<StructDecay>();
		for(int i = 0;i<datasourceDecays.size();i++)
		{
			StructDecay strcutDecay = new StructDecay();   //保存查询到的该条核素信息
			strcutDecay.setParentMass(datasourceDecays.elementAt(i).getParentMass());
			strcutDecay.setParentName(datasourceDecays.elementAt(i).getParentName());
			strcutDecay.setParentAtomNumber(datasourceDecays.elementAt(i).getParentAtomNumber());
			strcutDecay.setParentAtomMass(datasourceDecays.elementAt(i).getParentAtomMass());
			strcutDecay.setParentChineseName(datasourceDecays.elementAt(i).getParentChineseName());
			//示例化子核的相关信息
			Vector<SonNuclide> son = new Vector<SonNuclide>();
			for(int j = 0;j<datasourceDecays.elementAt(i).getSoNuclides().size();j++)
			{
				//根据子核的名字判断是否查询到
				if(datasourceDecays.elementAt(i).getSoNuclides().elementAt(j).getSonName().equals(nuclidename))
				{
					son.add(datasourceDecays.elementAt(i).getSoNuclides().elementAt(j));

				}
			}
			//如果子核的数组的大小为零，那么就是代表没有查询到，所有不用保存
			if(son.size()!=0)
			{
				strcutDecay.setSoNuclides(son);
				resultdecays.add(strcutDecay);
			}
		}
		return resultdecays;
	}

	/*
	 * 函数功能：   通过子核的质量数查询
	 */
	private Vector<StructDecay> findBySonMass(String nuclideMass)
	{
		Vector<StructDecay> resultdecays = new Vector<StructDecay>();  //保存查询到的核素信息
		for(int i = 0;i<datasourceDecays.size();i++)
		{
			StructDecay strcutDecay = new StructDecay();  //查询到的核素
			
			strcutDecay.setParentMass(datasourceDecays.elementAt(i).getParentMass());
			strcutDecay.setParentName(datasourceDecays.elementAt(i).getParentName());
			strcutDecay.setParentAtomNumber(datasourceDecays.elementAt(i).getParentAtomNumber());
			strcutDecay.setParentAtomMass(datasourceDecays.elementAt(i).getParentAtomMass());
			strcutDecay.setParentChineseName(datasourceDecays.elementAt(i).getParentChineseName());
			//实例化一个子核的数组，用来保存查询到的子核
			Vector<SonNuclide> son = new Vector<SonNuclide>();
			for(int j = 0;j<datasourceDecays.elementAt(i).getSoNuclides().size();j++)
			{
				//根据子核的质量数来查询
				if(datasourceDecays.elementAt(i).getSoNuclides().elementAt(j).getSonMass().equals(nuclideMass))
				{
					son.add(datasourceDecays.elementAt(i).getSoNuclides().elementAt(j));

				}
			}
			//如果查询到的子核大小为零那么久不需要保存
			if(son.size()!=0)
			{
				strcutDecay.setSoNuclides(son);
				resultdecays.add(strcutDecay);
			}
		}
		return resultdecays;
	}

}
