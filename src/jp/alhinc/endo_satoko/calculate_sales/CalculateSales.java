package jp.alhinc.endo_satoko.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	//ファイルソートメソッド
	public static boolean outFileWriter(String dirpath, String fileName, HashMap<String,String>nameMap,HashMap<String,Long>saleMap){

		List<Map.Entry<String,Long>> sortlist = new ArrayList<Map.Entry<String,Long>>(saleMap.entrySet());
		Collections.sort(sortlist, new Comparator<Map.Entry<String,Long>>(){
			public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
		});

		BufferedWriter brBuffer = null;
		//System.out.println(fileName);


		try{
			String sep = System.getProperty("line.separator");
			File result = new File(dirpath,fileName);
			result.createNewFile();
			FileWriter brWriter = new FileWriter(result);
			brBuffer = new BufferedWriter(brWriter);
			for(Map.Entry<String, Long> bs : sortlist){
				brBuffer.write(bs.getKey()+","+ nameMap.get(bs.getKey())+","+bs.getValue()+sep);

				String bsvalue = String.valueOf(bs.getValue());
				if(bsvalue.matches("\\d{10,}")){
					System.out.println("合計金額が10桁を超えました");
					return false;
				}
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try {
				if(brBuffer!=null){
					brBuffer.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}

		return true;
	}


	//fileReaderメソッド
	public static boolean lstfileReader(String dirpath, String fileName, String pattern, String whichError, HashMap<String,String> nameMap, HashMap<String,Long> saleMap){

		BufferedReader br = null;
		String s;


		//支店定義ファイルの読み込み
		try{
			File file = new File(dirpath,fileName);
			if(!file.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return false;
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);;
			while((s=br.readLine()) !=null){

				// sをコンマで区切り、支店コードと支店名に分割して、配列branchnameにぶちこむ
				String[] name = null;
				name= s.split(",",-1);
				if(name.length!=2){
					return false;
				}

				//支店定義ファイルのエラー処理
				if(! name[0].matches(pattern)){
					System.out.println(whichError+ "定義ファイルのフォーマットが不正です");
					return false;
				}
					nameMap.put(name[0],name[1] );
					saleMap.put(name[0],0L);//0Lにしておかないと0がintとして処理される
				
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		} finally {
			try {
				if(br !=null){
					br.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");;
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {

		//支店コードをkey、支店名をvalueにしたマップ
		HashMap<String,String> branchmap = new HashMap<String,String>();
		//商品コードをkey、商品名をvalueにしたマップ
		HashMap<String,String> commonditymap = new HashMap<String,String>();
		//支店コードをkey、売り上げをvalueにしたマップ.売り上げはLong型
		HashMap<String,Long> branchsalemap = new HashMap<String,Long>();
		//商品コードをkey、売り上げをvalueにしたマップ
		HashMap<String,Long> commonditysalemap = new HashMap<String,Long>();

		//ファイル読み込みメソッド呼び出し


		if(!lstfileReader(args[0],"branch.lst","^\\d{3}$","支店",branchmap,branchsalemap)){
			return;
		}
		if(!lstfileReader(args[0],"commondity.lst","[a-zA-Z0-9]{8}$","商品",commonditymap,commonditysalemap)){
			return;
		}


		//集計
		File file = new File(args[0]);
		File [] filelist=file.listFiles();


		//File型のListを作成、配列filelistを変換
		List<File> list=Arrays.asList(filelist);

		//System.out.println(+filelist.length);
		ArrayList<File> chosenlist = new ArrayList<File>();
		for(int i = 0; i<filelist.length;i++){
			filelist[i].getName();
			if(filelist[i].getName().matches("\\d{8}.rcd$")){//数字が8桁で末尾に.rcdを含む
				chosenlist.add(filelist[i]);
			}

			//歯抜けになっている場合の処理
			for(int num =0; num< chosenlist.size(); num++){
				String [] filename = filelist[num].getName().split("\\.");
				int filenumber = Integer.parseInt(filename[0]);
				if(!((num +1) == filenumber)){
					System.out.println("売り上げファイル名が連番になっていません");
					return;
				}
			}
		}

		//売り上げファイルの読み込み;

		//ループをまわして新しいリストに足していく
		BufferedReader br = null;
		try {
			for(int i=0; i<chosenlist.size();i++){
				ArrayList<String> saleslist = new ArrayList<String>();
				FileReader fr = new FileReader(chosenlist.get(i));
				br = new BufferedReader(fr);
				String contents;

				//System.out.println(chosenlist.size());

				//支店の合計金額、商品の合計金額に足していく

				while((contents = br.readLine()) !=null){
					saleslist.add(contents);
				}
				long sale = Long.parseLong(saleslist.get(2));

				long branchsales = branchsalemap.get(saleslist.get(0));
				long branchsum = branchsales += sale;

				long commonditysales = commonditysalemap.get(saleslist.get(1));
				long commonditysum = commonditysales += sale;

				//マップに入れる
				branchsalemap.put(saleslist.get(0),branchsum);
				commonditysalemap.put(saleslist.get(1), commonditysum);

				//売り上げファイルの支店コードが支店定義ファイルに存在しない場合
				if(!branchmap.containsKey(saleslist.get(0))){
					System.out.println(chosenlist.get(i).getName()+"の支店コードが不正です");
					return;
				}

				//売り上げファイルの商品コードが商品定義ファイルに存在しない場合
				if(!commonditymap.containsKey(saleslist.get(1))){
					System.out.println(chosenlist.get(i).getName()+"の商品コードが不正です");
					return;
				}
			}

		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

		}

		//outFileWriterメソッドでファイル出力
		if(!outFileWriter(args[0], "branch.out", branchmap, branchsalemap)){
			return;
		}
		if(!outFileWriter(args[0],"commondity.out",commonditymap,commonditysalemap)){
			return;

		}
		

	}
}


