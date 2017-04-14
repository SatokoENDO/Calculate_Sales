package jp.co.alh.endo_satoko.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Culculate_sales {
	public static void main(String[] args) {

		HashMap<String,String> branchmap = new HashMap<String,String>();
		String origin;

		File branch= new File(args[0]);
		if(args.length != 1){
			System.out.println("支店定義ファイルが存在しません");
		}

		//支店定義ファイルの読み込み
		try{
			File file = new File(args[0],"branch.lst");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;

			while((s=br.readLine()) !=null){

				// sをコンマで区切り、番号と支店名に分割して、配列branchnameにぶちこむ
				String [] branchname = s.split(",");
				branchmap.put("支店コード", branchname[0]);
				branchmap.put("支店名", branchname[1]);
				System.out.println(branchmap.entrySet());
			}
		}catch(IOException e){
			System.out.println(e);
		}




		//商品定義ファイルの読み込み
		try{
			File file = new File(args[0],"commondity.lst");
			if(args.length != 1){
				System.out.println("商品定義ファイルのフォーマットが不正です");
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;

			//読み込んだ文字列がnullじゃなくなるまで表示
			while((s=br.readLine()) !=null){
				System.out.println(s +"を読み込みました");
				// sをコンマで区切り、番号と支店名に分割して、配列branchbunkatuにぶちこむ
				String [] commondityyyy = s.split(",");
				for (int i = 0 ; i < commondityyyy.length ; i++){
					System.out.println(i + "番目の要素 = :" + commondityyyy[i]);
				}
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}

}

