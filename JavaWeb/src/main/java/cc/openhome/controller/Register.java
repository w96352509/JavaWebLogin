package cc.openhome.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class Register extends HttpServlet {

	// 設定儲存目標位置(成功/失敗位置(@webServlet))
	private final String USERS = "C:/Users/vic/eclipse-workspace/JavaWeb/users";
	private final String SUCCESS_PATH = "register_success.view";
	private final String ERROR_PATH = "register_error.view";
	// 驗證規格
	private final Pattern emailRegex = Pattern.compile("^[_a-z0-9-]+([.][_a-z0-9-]+)*@[a-z0-9-]+([.][a-z0-9-]+)*$");
	private final Pattern passwdRegex = Pattern.compile("^\\w{8,16}$");
	private final Pattern usernameRegex = Pattern.compile("^\\w{1,16}$");

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 接收屬性
		String email = req.getParameter("email");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String password2 = req.getParameter("password2");
		// 不正確的陣列
		List<String> errors = new ArrayList<>();
		if (!validateEmail(email)) {
			errors.add("不正確格式或空白(郵件)");
		}
		if (!validateUsername(username)) {
			errors.add("不正確格式或空白(帳號)");
		}
		if (!validdatePassword(password, password2)) {
			errors.add("不正確格式或空白(密碼)");
		}
		
		String path;
		if (errors.isEmpty()) {
			path = SUCCESS_PATH;
			tryCreateUser(email, username, password);
		}else {
			path = ERROR_PATH;
			req.setAttribute("errors", errors);
		}
		req.getRequestDispatcher(path).forward(req, resp);
	}

	// 各種判斷方法
	private boolean validateEmail(String email) {
		return email != null && emailRegex.matcher(email).find();
	}

	private boolean validateUsername(String username) {
		return username != null && usernameRegex.matcher(username).find();
	}

	private boolean validdatePassword(String password, String password2) {
		return password != null && passwdRegex.matcher(password).find() && password.equals(password2);
	}

	// 創建帳號
	private void createUser(Path userhome, String email, String password) throws IOException {
		// 創建目錄
		Files.createDirectories(userhome); 
        // 亂碼
		Integer salt = ThreadLocalRandom.current().nextInt();
		// 加密
		String encrypt = String.valueOf(salt + password.hashCode());
        // 創建目錄檔案
		Path profile = userhome.resolve("profile"); 

		//讀取成檔案
		try (BufferedWriter writer = Files.newBufferedWriter(profile)) {
			//轉型態(String)
			writer.write(String.format("%s\t%s\t%d", email, encrypt, salt));
			//format
		}
	}
    
	//創立完整帳號
	private void tryCreateUser(String email, String username, String password) throws IOException {
		String fileSavingFolder = USERS ;
		/*File folder = new File(fileSavingFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}*/
		//得到存檔位置並加入帳號
		Path userhome = Paths.get(USERS, username);
		//如果帳號不在其中並建立
		if (Files.notExists(userhome)) {
			createUser(userhome, email, password);
		}
	}

}
