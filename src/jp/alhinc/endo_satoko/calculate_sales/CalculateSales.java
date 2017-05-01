package jp.alhinc.endo_satoko.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	//ファイル読み込みメソッド
	public static boolean lstfileReader(String dirPath, String fileName, String pattern, String whichError, HashMap<String,String> nameMap, HashMap<String,Long> saleMap){

		BufferedReader br = null;



		//支店定義ファイルの読み込み
		try{
			if(dirPath == null){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}

			File file = new File(dirPath,fileName);
			if(!file.exists()){
				System.out.println(whichError + "定義ファイルが存在しません");
				return false;
			}

			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);;

			String s;

			while((s = br.readLine()) != null){

				// sをコンマで区切り、支店コードと支店名に分割して、配列nameにぶちこむ
				String[] name = null;
				name = s.split(",", -1);
				if (name.length != 2){
					System.out.println(whichError + "定義ファイルのフォーマットが不正です");
					return false;
				}

				//支店定義ファイルのエラー処理
				if( name[0].matches(pattern)){
					nameMap.put(name[0],name[1] );
					saleMap.put(name[0],0L);//0Lにしておかないと0がintとして処理される
				}else{
					System.out.println(whichError + "定義ファイルのフォーマットが不正です");
					return false;
				}
			}

		} catch(FileNotFoundException e){
			System.out.println(whichError + "定義ファイルが存在しません");
			return false;
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally {
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");;
				return false;
			}
		}
		return true;
	}


	//ファイルソートメソッド
	public static boolean outFileWriter(String dirPath, String fileName, HashMap<String,String>nameMap,HashMap<String,Long>saleMap){


		BufferedWriter brBuffer = null;
		//System.out.println(fileName);


		try{
			File result = new File(dirPath,fileName);
			result.createNewFile();
			FileWriter brWriter = new FileWriter(result);
			brBuffer = new BufferedWriter(brWriter);

			List<Map.Entry<String,Long>> sortlist = new ArrayList<Map.Entry<String, Long>>(saleMap.entrySet());
			Collections.sort(sortlist, new Comparator<Map.Entry<String, Long>>(){
				public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});

			String sep = System.getProperty("line.separator");

			for(Map.Entry<String, Long> bs : sortlist){
				brBuffer.write(bs.getKey() + "," + nameMap.get(bs.getKey()) + "," + bs.getValue() + sep);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try {
				if(brBuffer != null){
					brBuffer.close();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}

		return true;
	}


	public static void main(String[] args) {

		//支店コードをkey、支店名をvalueにしたマップ
		HashMap<String, String> branchmap = new HashMap<String, String>();
		//商品コードをkey、商品名をvalueにしたマップ
		HashMap<String, String> commoditymap = new HashMap<String, String>();
		//支店コードをkey、売り上げをvalueにしたマップ.売り上げはLong型
		HashMap<String, Long> branchsalemap = new HashMap<String, Long>();
		//商品コードをkey、売り上げをvalueにしたマップ
		HashMap<String, Long> commoditysalemap = new HashMap<String, Long>();

		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		//ファイル読み込みメソッド呼び出し
		if(!lstfileReader(args[0], "branch.lst","^\\d{3}$", "支店", branchmap, branchsalemap)){
			return;
		}

		if(!lstfileReader(args[0], "commodity.lst", "[a-zA-Z0-9]{8}$", "商品",commoditymap, commoditysalemap)){
			return;
		}

		//売り上げファイルの読み込み;
		BufferedReader br = null;

		try {
			//集計
			File file = new File(args[0]);
			File [] filelist=file.listFiles();

			//System.out.println(+filelist.length);

			ArrayList<File> chosenlist = new ArrayList<File>();
			for(int i = 0; i<filelist.length; i++){
				filelist[i].getName();


				//売上ファイル名の限定
				if(filelist[i].getName().matches("^[0-9]{8}.rcd$") && filelist[i].isFile()){//数字が8桁で末尾に.rcdを含む
					chosenlist.add(filelist[i]);
				}
			}

			//売上ファイル名が歯抜けになっている場合の処理
			for(int num = 0; num< chosenlist.size(); num++){
				String [] filename = chosenlist.get(num).getName().split("\\.");
				int filenumber = Integer.parseInt(filename[0]);
				if(!((num +1) == filenumber)){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}

			for(int i=0; i<chosenlist.size(); i++){
				ArrayList<String> saleslist = new ArrayList<String>();
				File salesfile = chosenlist.get(i);
				FileReader fr = new FileReader(salesfile);
				br = new BufferedReader(fr);
				String contents;

				//System.out.println(chosenlist.size());

				//支店の合計金額、商品の合計金額に足していく

				while((contents = br.readLine()) != null){
					saleslist.add(contents);
				}

				if(saleslist.size() != 3){
					System.out.println(chosenlist.get(i).getName() + "のフォーマットが不正です");
					return;
				}

				long sale = Long.parseLong(saleslist.get(2));


				//売り上げファイルの支店コードが支店定義ファイルに存在しない場合
				if(!branchmap.containsKey(saleslist.get(0))){
					System.out.println(chosenlist.get(i).getName() + "の支店コードが不正です");
					return;
				}
				long branchsales = branchsalemap.get(saleslist.get(0));
				long branchsum = branchsales += sale;


				if(branchsum > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return ;
				}

				branchsalemap.put(saleslist.get(0), branchsum);

				//売上ファイルの商品コードが商品定義ファイルに存在しない場合
				if(!commoditymap.containsKey(saleslist.get(1))){
					System.out.println(chosenlist.get(i).getName() + "の商品コードが不正です");
					return;
				}

				long commoditysales = commoditysalemap.get(saleslist.get(1));
				long commoditysum = commoditysales += sale;

				if(commoditysum>9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return ;
				}

				commoditysalemap.put(saleslist.get(1), commoditysum);

			}

		} catch (FileNotFoundException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch (NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		//outFileWriterメソッドでファイル出力
		if(!outFileWriter(args[0], "branch.out", branchmap, branchsalemap)){
			return;
		}
		if(!outFileWriter(args[0],"commodity.out",commoditymap, commoditysalemap)){
			return;

		}
	}
}


