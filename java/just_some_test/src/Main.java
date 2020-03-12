public class Main {
	public static void main(String [] args){
		String memberCookie = "";
		String cookieMemberKey = (memberCookie == null) ? null : "aaaaa";

		System.out.println(cookieMemberKey);

		String d = "dd";
		String c = "c";
		if(!"".equals(d) && !d.equals(c)){

			System.out.println("not null");
		}
	}
}
