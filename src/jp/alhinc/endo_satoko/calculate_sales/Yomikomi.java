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

public class Yomikomi {

	//ファイルソートメソッド
	public static boolean outFileWriter(String dirpath, String fileName, HashMap<String,String>namemap,HashMap<String,Long>salemap){

		List<Map.Entry<String,Long>> sortlist = new ArrayList<Map.Entry<String,Long>>(salemap.entrySet());
		Collections.sort(sortlist, new Comparator<Map.Entry<String,Long>>(){
			public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
		});

		BufferedWriter brBuffer = null;
		try{
			File branchresult = new File(dirpath,fileName);
			branchresult.createNewFile();
			FileWriter brWriter = new FileWriter(branchresult);
			brBuffer = new BufferedWriter(brWriter);
			for(Map.Entry<String, Long> bs : sortlist){
				brBuffer.write(bs.getKey()+","+ namemap.get(bs.getKey())+","+bs.getValue()+"\n");

				String bsvalue = String.valueOf(bs.getValue());
				if(bsvalue.matches("\\d{10,}")){
					System.out.println("合計金額が10桁を超えました");
					return false;
				}
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			try {
				brBuffer.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		List<Map.Entry<String,Long>> commonditysortlist = new ArrayList<Map.Entry<String,Long>>(salemap.entrySet());
		Collections.sort(commonditysortlist, new Comparator<Map.Entry<String,Long>>(){
			public int compare (Map.Entry<String,Long> entry1, Map.Entry<String,Long> entry2){
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
		});
		return false;
	}


	//fileReaderメソッド
	public static boolean fileReader(String dirpath, String fileName, String pattern,HashMap<String,String> codeMap, HashMap<String,Long> nameMap){

		BufferedReader br = null;
		String origin;
		String[] name = null;

		//支店定義ファイルの読み込み
		try{
			File file = new File(dirpath,fileName);
			if(!file.exists()){
				System.out.println("支店定義ファイルが存在しません");
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);;
			while((origin=br.readLine()) !=null){

				// sをコンマで区切り、支店コードと支店名に分割して、配列branchnameにぶちこむ
				name= origin.split(",");


				//支店定義ファイルのエラー処理
				if(! name[0].matches(pattern)){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return false;
				}else{
					if(!name[0].matches(pattern)){
						System.out.println("商品定義ファイルのフォーマットが不正です");
						return false;
					}else{
						codeMap.put(name[0],name[1] );
					}
					nameMap.put(name[0],0L);//0Lにしておかないと0がintとして処理される
				}
			}

			//System.out.println(branchmap.entrySet());
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
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
		fileReader(args[0],"branch.lst","^\\d{3}$",branchmap,branchsalemap);
		fileReader(args[0],"commondity.lst","[a-zA-Z0-9]{8}$",commonditymap,commonditysalemap);


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
		}

		//売り上げファイルの読み込み;
		for(int i=0; i<chosenlist.size();i++){
			//ループをまわして新しいリストに足していく
			BufferedReader br = null;
			try {
				ArrayList<String> saleslist = new ArrayList<String>();
				FileReader fr = new FileReader(chosenlist.get(i));
				br = new BufferedReader(fr);
				String contents;


				//支店の合計金額、商品の合計金額に足していく
				try {
					while((contents = br.readLine()) !=null){
						saleslist.add(contents);;
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
					if(!branchmap.containsKey(saleslist.get(0)))
					{
						System.out.println(chosenlist.get(i).getName()+"の支店コードが不正です");
					}

					//売り上げファイルの商品コードが商品定義ファイルに存在しない場合
					if(!commonditymap.containsKey(saleslist.get(1)))
					{
						System.out.println(chosenlist.get(i).getName()+"の商品コードが不正です");
					}
				} catch (IOException e) {
					System.out.println(e);
					e.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}

			} catch (FileNotFoundException e) {
				System.out.println("予期せぬエラーが発生しました");// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		//outFileWriterメソッドでファイル出力
		outFileWriter(args[0], "branch.out", branchmap, branchsalemap);
		outFileWriter(args[0],"commondity.out",commonditymap,commonditysalemap);


	}
}




