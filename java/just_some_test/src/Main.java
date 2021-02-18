public class Main {
	public static void main(String [] args){
		String memberCookie = "";
		String memberKey = " bbbbb";
		String cookieMemberKey = (memberCookie == null) ? null : memberCookie;

//		if(memberKey == null || "".equals(memberKey))
//			System.out.println(memberKey);

		if(memberKey.isEmpty() || "".equals(memberKey)){
			System.out.println("return new EntityMap()");
			return;
		}
//		else {
			if(!memberKey.trim().equals(cookieMemberKey)){
//			if(!(memberKey.trim().equals("ccccc"))){

				if(cookieMemberKey == null) return;
				if(cookieMemberKey.isEmpty() || "".equals(cookieMemberKey)) return;
				System.out.println(!cookieMemberKey.equals("s"));
				System.out.println(!"s".equals(cookieMemberKey));
				System.out.println("s".equals("") == true);

				System.out.println(memberKey.trim().equals(cookieMemberKey));
				System.out.println("different person");
				System.out.println("return new EntityMap()");
			}
			else{
				System.out.println("Normal Case");
			}
//		}

//		String d = "dd";
//		String c = "c";
//		if(!"".equals(d) && !d.equals(c)){
//
//			System.out.println("not null");
//		}
	}
}
