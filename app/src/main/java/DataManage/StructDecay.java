package DataManage;

import java.util.Vector;


public class StructDecay {

	private String parentName;
	private String parentMass;
	private String parentAtomNumber;
	private String parentAtomMass;
	private String parentChineseName;
	private Vector<SonNuclide> soNuclides;

	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getParentMass() {
		return parentMass;
	}
	public void setParentMass(String parentMass) {
		this.parentMass = parentMass;
	}

	public String getParentAtomNumber() {
		return parentAtomNumber;
	}
	public void setParentAtomNumber(String parentAtomNumber) {
		this.parentAtomNumber = parentAtomNumber;
	}
	public String getParentAtomMass() {
		return parentAtomMass;
	}
	public void setParentAtomMass(String parentAtomMass) {
		this.parentAtomMass = parentAtomMass;
	}
	public String getParentChineseName() {
		return parentChineseName;
	}
	public void setParentChineseName(String parentChineseName) {
		this.parentChineseName = parentChineseName;
	}
	public Vector<SonNuclide> getSoNuclides() {
		return soNuclides;
	}
	public void setSoNuclides(Vector<SonNuclide> soNuclides) {
		this.soNuclides = soNuclides;
	}

}
