package com.iermu.client.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cms.iermu.cmsUtils;
import com.iermu.client.config.PathConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 文件处理
 * @author lanyj
 *
 */
public class FileUtil {

	private static final String TAG = "FileUtil";
	/**
	 * 根据文件路径 递归创建文件
	 * 
	 * @param file
	 */
	public static void createDipPath(String file) {
		String parentFile = file.substring(0, file.lastIndexOf("/"));
		File file1 = new File(file);
		File parent = new File(parentFile);
		if (!file1.exists()) {
			parent.mkdirs();
			try {
				file1.createNewFile();
				Logger.i(TAG, "Create new file :" + file);
			} catch (IOException e) {
				Logger.e(TAG, e.getMessage());
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 */
	public static boolean deleteFile(String path) {
		boolean bl;
		File file = new File(path);
		if (file.exists()) {
			bl = file.delete();
		} else {
			bl = false;
		}
		return bl;
	}
	/**
	 * 将bitmap保存到本地
	 * 
	 * @param bitmap
	 * @param imagePath
	 */
	@SuppressLint("NewApi")
	public static void saveBitmap(Bitmap bitmap, String imagePath,int s) {
		File file = new File(imagePath);
		createDipPath(imagePath);
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(imagePath.toLowerCase().endsWith(".png")){
			bitmap.compress(Bitmap.CompressFormat.PNG, s, fOut);
		}else if(imagePath.toLowerCase().endsWith(".jpg")){
			bitmap.compress(Bitmap.CompressFormat.JPEG, s, fOut);
		}else{
			bitmap.compress(Bitmap.CompressFormat.WEBP, s, fOut);
		}
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 复制文件
		public static void copyFile(String sourcePath, String toPath) {
			File sourceFile = new File(sourcePath);
			File targetFile = new File(toPath);
			createDipPath(toPath);
			try {
				BufferedInputStream inBuff = null;
				BufferedOutputStream outBuff = null;
				try {
					// 新建文件输入流并对它进行缓冲
					inBuff = new BufferedInputStream(
							new FileInputStream(sourceFile));

					// 新建文件输出流并对它进行缓冲
					outBuff = new BufferedOutputStream(new FileOutputStream(
							targetFile));

					// 缓冲数组
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = inBuff.read(b)) != -1) {
						outBuff.write(b, 0, len);
					}
					// 刷新此缓冲的输出流
					outBuff.flush();
				} finally {
					// 关闭流
					if (inBuff != null)
						inBuff.close();
					if (outBuff != null)
						outBuff.close();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 复制文件
		public static void copyFile(File sourceFile, File targetFile) {

			try {
				BufferedInputStream inBuff = null;
				BufferedOutputStream outBuff = null;
				try {
					// 新建文件输入流并对它进行缓冲
					inBuff = new BufferedInputStream(
							new FileInputStream(sourceFile));

					// 新建文件输出流并对它进行缓冲
					outBuff = new BufferedOutputStream(new FileOutputStream(
							targetFile));

					// 缓冲数组
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = inBuff.read(b)) != -1) {
						outBuff.write(b, 0, len);
					}
					// 刷新此缓冲的输出流
					outBuff.flush();
				} finally {
					// 关闭流
					if (inBuff != null)
						inBuff.close();
					if (outBuff != null)
						outBuff.close();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

    public static void downAppFile(final Context c, final String _urlStr) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(_urlStr);
        HttpResponse response;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            long m_FileSize = entity.getContentLength();
            InputStream is = entity.getContent();
            FileOutputStream fileOutputStream = null;
            if (is == null) {
                throw new RuntimeException("isStream is null");
            }

            String newFilename = _urlStr.substring(_urlStr.lastIndexOf("/")+1);
            fileOutputStream = c.openFileOutput(newFilename, Context.MODE_APPEND);
            byte[] buf = new byte[1024];
            int ch = -1;
            long m_DownSize = 0l;
            do {
                ch = is.read(buf);
                if (ch <= 0)
                    break;
                fileOutputStream.write(buf, 0, ch);
                m_DownSize+=ch;
            } while (true);
            fileOutputStream.flush();
            is.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getDownFile(Context context, String filePath) {
        FileInputStream ins = null;
        Bitmap bitmap = null;
        try {
            ins = context.openFileInput(filePath);
            byte[] bytes = new byte[ins.available()];
            ins.read(bytes);
            bitmap = Bytes2Bimap(bytes);
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(ins != null) try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    //byte 转bitmap
    public static Bitmap Bytes2Bimap(byte[] b){
        if(b.length!=0){
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        else {
            return null;
        }
    }

    //文件路径
    public static String getImagePath(String name) {
        String imagePath = PathConfig.CACHE_SHARE + "/" + name + ".jpeg";
        return imagePath;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

        /**
         * view 转换为pic
         */
    public static void saveAvatarBitmap(Bitmap bitmap, long currentTime) {
        if (bitmap == null) {
            return;
        }
        String imageName = currentTime + ".jpg";
        // 创建文件夹
        File fdir = new File(PathConfig.CACHE_AVATAR);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
        initBitmap(bitmap, fdir, imageName);
    }

    private static void initBitmap(Bitmap bitmap, File fdir, String imageName) {
// 创建文件
        File f = new File(fdir, imageName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 保存
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Logger.d("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.d("filePath:" + f.getPath());
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int percent, int size) {
        if (image == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, percent, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {  //循环判断如果压缩后图片是否大于sizekb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
