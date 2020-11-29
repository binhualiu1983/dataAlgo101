package picture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class App {
	public static void main(String[] args) throws Exception
	{
		rename();
	}
	
	public static void rename()
	{
		File folder = new File("D:\\Ѹ������\\all\\");
		for(File subFolders :folder.listFiles())
		{
			if(subFolders.isDirectory())
				continue;
			subFolders.renameTo(new File("D:\\Ѹ������\\all\\"+subFolders.getName()+"_"));
		}
	}
	
	public static void copyTo()
	{
		File file = new File("D:\\Ѹ������\\0.png");
		File folder = new File("D:\\Ѹ������\\all\\");
		for(File subFolders :folder.listFiles())
		{
			if(!subFolders.isDirectory())
				continue;
			try {
				FileUtils.copyFileToDirectory(file, subFolders);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void TransToSimple()
	{
		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		File folder = new File("D:\\Ѹ������\\all\\");
		for(File file :folder.listFiles())
		{
			if(!file.isDirectory())
				continue;
			String name = file.getName();
			String simpleName = converter.convert(name);
			if(!simpleName.equals(name))
			{
				file.renameTo(new File("D:\\Ѹ������\\all\\"+simpleName));
			}
		}
	}
	
	public static void moveTo() throws Exception {
		
		String[] folders = new String[]{
				//"D:\\Ѹ������\\1-50\\1-50",
				//"D:\\Ѹ������\\51-100\\51-100",
				//"D:\\Ѹ������\\101-150",
				//"D:\\Ѹ������\\151-200\\151-200",
				//"D:\\Ѹ������\\201-250\\201-250",
				//"D:\\Ѹ������\\251-300\\251-300",
				//"D:\\Ѹ������\\301-350\\301-350",
				//"D:\\Ѹ������\\351-400\\351-400",
				//"D:\\Ѹ������\\401-450\\401-450",
				//"D:\\Ѹ������\\451-500\\451-500",
				//"D:\\Ѹ������\\501-550\\501-550"
				//"D:\\Ѹ������\\551-600\\551-600",
				//"D:\\Ѹ������\\600-637\\600-637"
				};
		
		for(String folderStr : folders)
		{
			File folder = new File(folderStr);
			for(File file :folder.listFiles())
			{
				if(!file.isDirectory())
					continue;
				int indexOf = file.getName().indexOf("��");
				if(indexOf==-1)
					continue;
				String newName = file.getName().substring(indexOf+1);
				String targetFileStr = "D:\\Ѹ������\\all\\" +newName;
				File targetFile = new File(targetFileStr);
				if(targetFile.exists())
					continue;
				file.renameTo(targetFile);
			}
		}
		
	}
}
