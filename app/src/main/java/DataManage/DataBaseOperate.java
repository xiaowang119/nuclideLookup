package DataManage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Vector;

public class DataBaseOperate{
	private String databasepathString;    //数据库路径
	private String sqlString;

	//对数据库是否存在进行判断
	public DataBaseOperate(String dbpathString, boolean isLoad)
	{
		databasepathString = dbpathString;
		File dbFile = new File(databasepathString);
		try {
			if(!dbFile.exists())
				dbFile.createNewFile();
			else{
				if(isLoad){
					dbFile.delete();
					dbFile.createNewFile();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String str1 = databasepathString.substring(0,1).toLowerCase();
		String str2 = databasepathString.substring(1);
		databasepathString = str1 + str2;
		//sqlString = "jdbc:sqlite://" + databasepathString.replace('\\', '/');
		sqlString = databasepathString.replace('\\', '/');
	}

	public Vector<StructDecay> getAll()
	{
		Vector<StructDecay> decays = new Vector<StructDecay>();
		try {
			SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqlString, null);
			Cursor cursor = sqLiteDatabase.rawQuery("select * from decaymessage", null);
			Log.d("test", "getAll()  Cursor:列数： " + cursor.getColumnCount());
			int a = 0;
			//从数据库第一行开始遍历
			if (cursor.moveToFirst()) {
				//对每一种核素的具体信息进行详尽地整理
				do {
					String parentMass = cursor.getString(cursor.getColumnIndex("ParentMassNumber"));
					String parentName = cursor.getString(cursor.getColumnIndex("ParentName"));
					String parentAtomNumber= cursor.getString(cursor.getColumnIndex("ParentAtomNumber"));
					String parentAtomMass = cursor.getString(cursor.getColumnIndex("ParentAtomMass"));
					String parentChineseName = cursor.getString(cursor.getColumnIndex("ParentChineseName"));
					String[]sonMass = split(cursor.getString(cursor.getColumnIndex("DaughterMassNumber")),'|');
					String[]sonName = split(cursor.getString(cursor.getColumnIndex("DaughterName")),'|');
					String[] sonAtomNumberStrings = split(cursor.getString(cursor.getColumnIndex("DaughterAtomNumber")), '|');
					String[] sonAtomMass = split(cursor.getString(cursor.getColumnIndex("DaughterAtomMass")), '|');
					String[] sonChineseName = split(cursor.getString(cursor.getColumnIndex("DaughterChineseName")), '|');
					String[]halfLife = split(cursor.getString(cursor.getColumnIndex("HalfLife")),'|');
					String[]decayType = split(cursor.getString(cursor.getColumnIndex("DecayType")),'|');
					String[]decayIntensity = split(cursor.getString(cursor.getColumnIndex("DecayIntensity")),'|');
					String[]gammaEnergy = split(cursor.getString(cursor.getColumnIndex("GEnergy")),'|');
					String[]gammaIntensity = split(cursor.getString(cursor.getColumnIndex("GIntensity")),'|');
					String[]betaEnergy = split(cursor.getString(cursor.getColumnIndex("BEnergy")),'|');
					String[]betaIntensity = split(cursor.getString(cursor.getColumnIndex("BIntensity")),'|');
					String[]postiveBetaEnergy = split(cursor.getString(cursor.getColumnIndex("PostiveBEnergy")),'|');
					String[]postiveBetaIntensity = split(cursor.getString(cursor.getColumnIndex("PostiveBIntensity")),'|');
					String[]alphaEnergy = split(cursor.getString(cursor.getColumnIndex("AEnergy")),'|');
					String[] alphaIntensity = split(cursor.getString(cursor.getColumnIndex("AIntensity")), '|');
					String group = cursor.getString(cursor.getColumnIndex("duxing"));
					String level1 = cursor.getString(cursor.getColumnIndex("Level1"));
					String level2 = cursor.getString(cursor.getColumnIndex("Level2"));
					String level3 = cursor.getString(cursor.getColumnIndex("Level3"));
					String level4 = cursor.getString(cursor.getColumnIndex("Level4"));
					String level5 = cursor.getString(cursor.getColumnIndex("Level5"));

					//将每个核素打包成一个衰变结构体
					StructDecay structdecay = new StructDecay();
					structdecay.setParentMass(parentMass);
					structdecay.setParentName(parentName);
					structdecay.setParentAtomNumber(parentAtomNumber);
					structdecay.setParentAtomMass(parentAtomMass);
					structdecay.setParentChineseName(parentChineseName);

					a++;
					//将衰变得到的子体核素打包成衰变结构体内的一个向量
					Vector<SonNuclide> sons = new Vector<SonNuclide>();
					for(int i = 0;i<sonName.length;i++)
					{
						SonNuclide son = new SonNuclide();
						son.setSonName(sonName[i]);
						son.setSonMass(sonMass[i]);

						son.setHalfLife(halfLife[i]);

						son.setDecayTypeString(decayType[i]);

						//β衰变的能量和强度
						String[] benergy = split(betaEnergy[i],';');
						String[] bIntensity = split(betaIntensity[i],';');
						Vector<String> betaeVector = new Vector<String>();
						Vector<String> betaIVector = new Vector<String>();
						for(int k =0;k<benergy.length;k++)
						{
							if (!benergy[k].equals("")) {
								betaeVector.add(benergy[k]);
								betaIVector.add(bIntensity[k]);
							}
						}
						son.setBetaEnergyVector(betaeVector);
						son.setBetaIntenistyVector(betaIVector);

						//α衰变的能量和强度
						String[] aenergy = split(alphaEnergy[i],';');
						String[] aIntensity = split(alphaIntensity[i],';');
						Vector<String> alphaeVector = new Vector<String>();
						Vector<String> alphaIVector = new Vector<String>();
						for(int k =0;k<aenergy.length;k++)
						{
							if (!aenergy[k].equals("")) {
								alphaeVector.add(aenergy[k]);
								alphaIVector.add(aIntensity[k]);
							}

						}
						son.setAlphaEnergyVector(alphaeVector);
						son.setAlphaIntensityVector(alphaIVector);

						//β正衰变的能量和强度
						String[] pbenergy = split(postiveBetaEnergy[i],';');
						String[] pbIntensity = split(postiveBetaIntensity[i],';');
						Vector<String> pbeVector = new Vector<String>();
						Vector<String> pbIVector = new Vector<String>();
						for(int k =0;k<pbenergy.length;k++)
						{
							if (!pbenergy[k].equals("")) {
								pbeVector.add(pbenergy[k]);
								pbIVector.add(pbIntensity[k]);
							}
						}
						son.setPostiveBetaEnergyVector(pbeVector);
						son.setPostiveBetaIntensityVector(pbIVector);

						//γ衰变的能量和强度
						String[] genergy =split(gammaEnergy[i],';');
						String[] gIntensity = split(gammaIntensity[i],';');
						Vector<String> geVector = new Vector<String>();
						Vector<String> gIVector = new Vector<String>();
						for(int k =0;k<genergy.length;k++)
						{
							if (!genergy[k].equals("")) {
								geVector.add(genergy[k]);
								gIVector.add(gIntensity[k]);
							}
						}
						son.setGammaEnergyVector(geVector);
						son.setGammaIntensityVector(gIVector);
						sons.add(son);
					}
					structdecay.setSoNuclides(sons);
					//将各个衰变体打包成一个完整的衰变数据结构
					decays.add(structdecay);
				} while (cursor.moveToNext());
			}
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decays;
	}
	//自定义字符串分离函数
	private String[] split(String str, char c)
	{
		Vector<String> strs = new Vector<String>();
		String temp = "";
		if(str.isEmpty())
			strs.add(str);
		else
		{
			//boolean iscontain = false;
			int i = 0;
			for(;i<str.length();i++)
			{
				if(str.charAt(i)==c)
				{
					strs.add(temp);
					//iscontain = true;
					temp="";
				}else {
					temp = temp + str.charAt(i);
				}
			}
			if(i==str.length()) strs.add(temp);

		}
		//由于字串的数目不定，所以不能一开始就用字符数组；改而选择用可变长的向量
		String[] strings = new String[strs.size()];
		for(int k = 0;k<strs.size();k++)
		{
			strings[k] = strs.elementAt(k);
		}
		return strings;
	}

}
