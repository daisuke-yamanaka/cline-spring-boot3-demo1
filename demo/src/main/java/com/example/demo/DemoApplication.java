package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
		
		// コマンドライン引数からプロファイルを検出
		boolean isCommandMode = false;
		for (String arg : args) {
			if (arg.contains("command")) {
				isCommandMode = true;
				break;
			}
		}
		
		if (isCommandMode) {
			System.out.println("コマンドモードで起動します...");
			application.setAdditionalProfiles("command");
		} else {
			System.out.println("Webアプリケーションモードで起動します...");
		}
		
		application.run(args);
	}
}
