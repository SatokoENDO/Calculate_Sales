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
	public static boolean lstFileReader(String dirPath, String fileName, String pattern, String whichError, HashMap<String,String> nameMap, HashMap<String,Long> saleMap){

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

			List<Map.Entry<String,Long>> sortList = new ArrayList<Map.Entry<String, Long>>(saleMap.entrySet());
			Collections.sort(sortList, new Comparator<Map.Entry<String, Long>>(){
				public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});

			String sep = System.getProperty("line.separator");

			for(Map.Entry<String, Long> bs : sortList){
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
		HashMap<String, String> branchMap = new HashMap<String, String>();
		//商品コードをkey、商品名をvalueにしたマップ
		HashMap<String, String> commodityMap = new HashMap<String, String>();
		//支店コードをkey、売り上げをvalueにしたマップ.売り上げはLong型
		HashMap<String, Long> branchSaleMap = new HashMap<String, Long>();
		//商品コードをkey、売り上げをvalueにしたマップ
		HashMap<String, Long> commoditySaleMap = new HashMap<String, Long>();

		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		//ファイル読み込みメソッド呼び出し
		if(!lstFileReader(args[0], "branch.lst","^\\d{3}$", "支店", branchMap, branchSaleMap)){
			return;
		}

		if(!lstFileReader(args[0], "commodity.lst", "[a-zA-Z0-9]{8}$", "商品",commodityMap, commoditySaleMap)){
			return;
		}

		//売り上げファイルの読み込み;
		BufferedReader br = null;

		try {
			//集計
			File file = new File(args[0]);
			File [] fileList=file.listFiles();

			//System.out.println(+fileList.length);

			ArrayList<File> chosenList = new ArrayList<File>();
			for(int i = 0; i<fileList.length; i++){
				fileList[i].getName();


				//売上ファイル名の限定
				if(fileList[i].getName().matches("^[0-9]{8}.rcd$") && fileList[i].isFile()){//数字が8桁で末尾に.rcdを含む
					chosenList.add(fileList[i]);
				}
			}

			//売上ファイル名が歯抜けになっている場合の処理
			for(int num = 0; num< chosenList.size(); num++){
				String [] fileName = chosenList.get(num).getName().split("\\.");
				int fileNumber = Integer.parseInt(fileName[0]);
				if(!((num +1) == fileNumber)){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}

			for(int i=0; i<chosenList.size(); i++){
				ArrayList<String> salesList = new ArrayList<String>();
				File salesFile = chosenList.get(i);
				FileReader fr = new FileReader(salesFile);
				br = new BufferedReader(fr);
				String contents;

				//System.out.println(chosenList.size());

				//支店の合計金額、商品の合計金額に足していく

				while((contents = br.readLine()) != null){
					salesList.add(contents);
				}

				if(salesList.size() != 3){
					System.out.println(chosenList.get(i).getName() + "のフォーマットが不正です");
					return;
				}

				long sale = Long.parseLong(salesList.get(2));


				//売り上げファイルの支店コードが支店定義ファイルに存在しない場合
				if(!branchMap.containsKey(salesList.get(0))){
					System.out.println(chosenList.get(i).getName() + "の支店コードが不正です");
					return;
				}
				long branchsales = branchSaleMap.get(salesList.get(0));
				long branchSum = branchsales += sale;


				if(branchSum > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return ;
				}

				branchSaleMap.put(salesList.get(0), branchSum);

				//売上ファイルの商品コードが商品定義ファイルに存在しない場合
				if(!commodityMap.containsKey(salesList.get(1))){
					System.out.println(chosenList.get(i).getName() + "の商品コードが不正です");
					return;
				}

				long commoditySales = commoditySaleMap.get(salesList.get(1));
				long commoditySum = commoditySales += sale;

				if(commoditySum>9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return ;
				}

				commoditySaleMap.put(salesList.get(1), commoditySum);

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
		if(!outFileWriter(args[0], "branch.out", branchMap, branchSaleMap)){
			return;
		}
		if(!outFileWriter(args[0],"commodity.out",commodityMap, commoditySaleMap)){
			return;

		}
	}
}


