package MyUtil;

/**
 * 作为操作checkBox的工具类
 */
public class Model {
    private boolean ischeck;
    private String name;
    private String account;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount (String account) {
        this.account = account;
    }
}