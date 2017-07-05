package hz.mediaextractortest.utils;

import android.util.Log;

/**
 * Log统一管理类
 * 
 * @author way
 * 
 */
public class L
{

	private L()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
	private static final String TAG = "HzPlayer";

	// 下面四个是默认tag的函数
	public static void i(String className, String msg)
	{
		if (isDebug)
			Log.i(TAG, "[" + className + "]" + msg);
	}

	public static void d(String className, String msg)
	{
		if (isDebug)
			Log.d(TAG, "[" + className + "]" + msg);
	}

	public static void e(String className, String msg)
	{
		if (isDebug)
			Log.e(TAG, "[" + className + "]" + msg);
	}

	public static void v(String className, String msg)
	{
		if (isDebug)
			Log.v(TAG, "[" + className + "]" + msg);
	}

}