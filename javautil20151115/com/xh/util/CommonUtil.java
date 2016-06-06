package com.xh.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




/**
 * 工具类，包含一些常用的函数
 * <p>Title: </p>
 * @author adriftor
 * @version 1.1
 */
public class CommonUtil {
	public static final Log logger = LogFactory.getLog(CommonUtil.class);
	 private static Random random = new Random(System.currentTimeMillis());
	 
	    public static Class getGenericClass(Class clazz) {
		        return getGenericClass(clazz, 0);
	    }
	/**
	 * 
	 * @param clazz
	 * @param index 从0开始
	 * @return
	 */
	public static Class getGenericClass(Class clazz, int index) {
		// getGenericSuperclass()方法首先会判断是否有泛型信息，有那么返回泛型的Type，没有则返回Class，方法的返回类型都是Type
		Type genType = clazz.getGenericSuperclass();
		// ParameterizedType 表示参数化类型
		if (genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

			if ((params != null) && (params.length >= (index - 1))) {
				if (!(params[index] instanceof Class)) {
					return Object.class;
				}

				return (Class) params[index];
			}
		}
		return null;
	}
	
	public static Date addTime(int minute) {
		return addTime(new Date(),minute);
	}
	public static Date addTime(Date date,int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minute);
		return c.getTime();
	}
	public static Date addTime(String strDate,int minute) {
		return addTime(strToDate(strDate),minute);
	}
	public static Object getGenericClassBean(Class clazz) {
	        return getGenericClassBean(clazz, 0);
	}
	public static Object getGenericClassBean(Class clazz, int index) {
		try {
			Class c = getGenericClass(clazz, index);
			if (c != null) {
				return c.newInstance();
			} else {
				throw new AppException(clazz.getName() + "无法获得泛型类型");
			}
		} catch (Exception ex) {
			throw new AppException(ex);
		}

	}
  


    /**
     * 将str从字符集chFrom转换到字符集chTo
     * @param str
     * @param chFrom 原本字符集，可以为空
     * @param chTo 目标字符集合，不允许空
     * @return
     */
    public static String ch2(String str, String chFrom, String chTo) {
        if (chTo == null || chTo.trim().length() == 0)
            return str;
        if (str != null && str.length() > 0) {
            try {
                byte[] byteTmp;
                if (chFrom == null || chFrom.length() == 0)
                    byteTmp = str.getBytes();
                else
                    byteTmp = str.getBytes(chFrom);
                str = new String(byteTmp, chTo);
            }
            catch (Exception e) {
                logger.debug(" 从字符集 " + chFrom + " 转换为 " + chTo +
                          " 错误" +
                          e.toString());
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String dateToStr() {
        return dateToStr(19);
    }

    /**
     * 将当前日期转成字符串格式
     * @param len int 返回的字符串形式的日期的长度。此值决定了返回的字符串格式,对照如下：
     *      7:yyyy-MM
     *      8:yyyyMMdd
     *      10:yyyy-MM-dd
     *      14:yyyyMMddHHmmss
     *      其他：yyyy-MM-dd HH:mm:ss
     * @return String 字符串形式的日期
     */
    public static String dateToStr(int len) {
        Date date = Calendar.getInstance().getTime();
        return dateToStr(date, len);
    }

    public static String dateToStr(java.util.Date date) {
        return dateToStr(date, 19);
    }


    /**
     * 将日期date对象转成字符串格式
     * @param len int 返回的字符串形式的日期的长度。此值决定了返回的字符串格式,对照如下：
     *      7:yyyy-MM
     *      8:yyyyMMdd
     *      10:yyyy-MM-dd
     *      14:yyyyMMddHHmmss
     *      其他：yyyy-MM-dd HH:mm:ss
     * @param date 要转换的日期对象
     * @return String 字符串形式的日期
     */
    public static String dateToStr(java.util.Date date, int len) {
    	if (len == 7) { //yyyyMM
            java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyy-MM");
            return sf.format(date);
        }
    	else if (len == 8) { //yyyyMMdd
            java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMdd");
            return sf.format(date);
        }
        else if (len == 10) { //yyyy-MM-dd
            java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            return sf.format(date);
        }
        else if (len == 14) { //yyyyMMddHHmmss
            java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
            return sf.format(date);
        }
        else { //yyyy-MM-dd HH:mm:ss
            java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sf.format(date);
        }

    }

    /**
     * 返回限定长度的字符串，如果超过了限定长度的字符串，则返回截取指定长度的字符串，
     * @param str String
     * @param limit int 限定长度
     * @param strAppend String 超长时附加的字符串
     * @return String
     */
    public static String displayLimit(String str, int limit, String strAppend) {
        int len = str.length();
        if (len > limit) {
            str = str.substring(0, limit) + strAppend;
        }
        return str;

    }


    /**
     * 保留指定的小数位数，如果dValue现有的小数位数比xiaoShuWei小，则返回现有值
     * @param dValue
     * @param xiaoShuWei
     * @return
     */
    public static double doubleTrim(double dValue, int scale) {
        if (scale <= 0 || scale > 15)
            return dValue;
        long l = (long) (dValue * (Math.pow(10, scale)) + 0.5);
        double returnValue = l / Math.pow(10, scale);
        String str = "" + returnValue;
        int position = str.indexOf(".");
        if (position != -1) {
            String strCount = str.substring(position + 1);
            int count = strCount.length();
            if (count > scale) {
                str = str.substring(0, position + scale);
            }
        }
        return returnValue;
    }

    /**
     * 执行命令
     * @param args
     * @return
     */
    public static String execute(String args) throws Exception {
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(args);

            InputStream in = p.getInputStream();

            int exitcode = 0;
            try {
                exitcode = p.waitFor();
            }
            catch (InterruptedException e) {
                throw new Exception(e);
            }
            BufferedInputStream din = new BufferedInputStream(in);
            String str;
            int len;

            if ((len = din.available()) > 0) {
                byte[] chs = new byte[len];
                din.read(chs, 0, len);
                str = new String(chs);
            }
            else
                str = "error";
            din.close();
            return str;
            //return "";
        }
        catch (java.io.IOException e) {
            return e.toString();
        }
    }

    /**
     * 统计字符串str里字符串inner出现的次数
     * @param str String
     * @param inner String
     * @return int
     */
    public static int getCount(String str, String inner) {
        int count = 0;
        int len = str.length();
        int startDot = 0;
        int dot = -1;
        while (true) {
            dot = str.indexOf(inner, startDot);
            if (dot != -1) {
                count++;
                startDot = dot + 1;
                if (startDot >= len)
                    break;
            }
            else {
                break;
            }
        }
        return count;
    }

    /**
     * 获得指定日期指定天数差的日期字符串
     * @param dateStr 格式为:yyyymmdd或yyyymmddhhmmss
     * @param dayCount 天数
     * @return 返回同dateStr格式相同的日期字符串
     */
    public static String getDateStrDiff(String strDate, int dayCount) {
        Date date = strToDate(strDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, dayCount);
        String rStr = null;
        date = c.getTime();

        if (strDate.length() == 8 && strDate.indexOf("-") == -1) { //yyyymmdd
            rStr = dateToStr(date, 8);
        }
        else if (strDate.length() < 11) { //yyyy-m-d
            rStr = dateToStr(date, 10);
        }
        else if (strDate.length() == 14 && strDate.indexOf(":") == -1) { //yyyymmddhhmiss
            rStr = dateToStr(date, 14);
        }
        else if (strDate.length() >= 14 && strDate.indexOf(":") != -1) { //yyyy-m-d h:m:getString
            rStr = dateToStr(date);
        }
        else {
            throw new AppException("无效的日期格式：" + strDate);
        }

        return rStr;
    }

    /**
     * 计算日期间隔天数差
     * @param date1
     * @param date2
     * @return
     */
    public static int getDayDiff(Date date1, Date date2) {
        return (int) ((date1.getTime() - date2.getTime()) / (3600 * 24 * 1000));
    }

    /**
     * 计算日期间隔天数差
     * @param date1 格式为：yyyymmdd
     * @param date2 格式为：yyyymmdd
     * @return
     */
    public static int getDayDiff(String strDate1, String strDate2) {
        return getDayDiff(strToDate(strDate1), strToDate(strDate2));
    }

    public static String getDayOfWeekForChinese(Date date){
		String rel = "";
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int i = cal.get(Calendar.DAY_OF_WEEK);
			switch(i){
				case 1:{
					rel="星期日";
					break;
				}
				case 2:{
					rel="星期一";
					break;
				}case 3:{
					rel = "星期二";
					break;
				}case 4:{
					rel = "星期三";
					break;
				}case 5:{
					rel = "星期四";
					break;
				}case 6:{
					rel = "星期五";
					break;
				}case 7:{
					rel = "星期六";
					break;
				}default:
					rel = "";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rel;
	}


    /**
     * 计算日期间的小时差
     * @param date1
     * @param date2
     * @return
     */
    public static long getHourDiff(Date date1, Date date2) {
        return ((long) (date1.getTime() - date2.getTime())) / (3600 * 1000);
    }

    /**
	 * 返回指定字节数组的MD5数字签名
	 * 
	 * @param data
	 *            要签名的字符串
	 * @return MD5签名的字节数组
	 */
	public static byte[] getMD5(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			md.update(data.getBytes());
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			logger.error( "getMessageDigest(): " + e);
		}
		return null;
	}


    /**
	 * 返回指定字符串的MD5数字签名
	 * 
	 * @param data
	 *            要签名的字符串
	 * @return MD5签名的字符串
	 */
	public static String getMD5String(String data) {
		return new BigInteger(1, getMD5(data)).toString(16);
	}

    /**
     * 计算日期间的分钟差
     * @param date1
     * @param date2
     * @return
     */
    public static long getMinuteDiff(Date dateBig, Date dateSmall) {
        return ((long) (dateBig.getTime() - dateSmall.getTime())) / (60 * 1000);
    }


    /**
     * 分离出2个标志字符串之间的值
     * 如a[f1]kfd[f2]fd,分离出[f1]和[f2]之间的值"kfd"
     * @param str String
     * @param chBegin String 开始标志字符串，如例子里的"["
     * @param chEnd String 结束标志字符串，如例子里的"]"
     * @param containCh boolean 分离结果是否包含开始和结束标志字符串,如值为true,那么在例子中
     * Record的值就是[f1]; 如果为false 那么在例子中Record的值就是f1
     * @return Record
     */
    public static Record getSubstr(String str, String chBegin,
                                   String chEnd, boolean containCh) {
        Record rd = new Record();
        int index = 0;
        while (str.length() > 0) {
            if (str.indexOf(chBegin) != -1) {
                str = str.substring(str.indexOf(chBegin));
            }
            else {
                str = "";
                break;
            }
            if (str.indexOf(chEnd) != -1) {
                String xiang = str.substring(0,
                                             str.indexOf(chEnd) + chEnd.length());
                str = str.substring(str.indexOf(chEnd) + chEnd.length());
                if (containCh) {
                    rd.set("_"+(index++), xiang.trim());
                }
                else {
                    rd.set("_"+(index++),
                             (xiang.substring(chBegin.length(),
                                              xiang.length() - chEnd.length())).
                             trim());
                }
            }
            else {
                str = "";
                break;
            }
        }
        return rd;
    }

    /**
     * JVM缺省状态下，有许多环境变量，getSystemSetting()方法通过构造对象：
     * @return
     */
    public static String getSystemSetting() {
        StringBuffer sb = new StringBuffer();
        Properties syspro = System.getProperties();
        Enumeration es = syspro.keys();
        while (es.hasMoreElements()) {

            String theKey = (String) es.nextElement();
            String theValue = syspro.getProperty(theKey);
            sb.append(theKey + "=" + theValue + "\n");

        }
        logger.debug(sb.toString());
        return new String(sb);

    }

    /**
     * 保留两位小数，四舍五入
     * @param d
     * @return
     */
    public static double iPrecision(double d, int i) {
        long l = (long) (d * (10 ^ i) + 0.5);
        return l / 10 ^ i;
    }

    /**
     * 是否为空。
     * @param str
     * @return 字符串为null、长度为空的字符串或都有空格组成的字符串，都返回true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.trim().length()==0;
    }
    /**
     * 是否为空或为0。
     * @param str
     * @return 字符串为null、长度为空的字符串或都有空格组成的字符串，都返回true
     */
    public static boolean isEmptyOrZero(String str) {
        return str == null || str.length() == 0 || str.trim().length()==0 || str.trim().equals("0");
    }
    /**
     * 检查是否小写字符或数字组成的字符串
     * @param str String
     * @return boolean
     */
    static public boolean isLetterOrDigitString(String str) {
        if (str == null)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)))
                return false;
        }
        return true;
    }
    
    /**
     * 检查是否都是小写字符组成的字符串
     * @param str String
     * @return boolean
     */
    static public boolean isLetterString(String str) {
        if (str == null)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i)))
                return false;
        }
        return true;
    }
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    public static boolean isNotEmptyOrZero(String str) {
        return !isNotEmptyOrZero(str);
    }
    /**
     * 验证是否是有效的数值字符串
     * @param str String
     * @return boolean
     */
    static public boolean isNumberString(String str) {
        if (str == null || str.trim().equals(""))
            return false;
        try {
            Double.parseDouble(str);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }




    /**
     * 是否是纯数字。不能含有+ - .等，就是有0123456789十个数字构成的字符串
     * @param str String
     * @return boolean
     */
    static public boolean isSimpleNumber(String str) {
        if (str == null || str.trim().equals(""))
            return false;

        int size = str.length();
        for (int i = 0; i < size; i++) {
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是完全有数字组成的字符串，不包括点号、正负符号。如”13243243“返回true,"-111","2.3"返回false
     * @param str
     * @return
     */
    static public boolean isWholeNumberString(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
    	}
        int len = str.length();
        for (int i = 0;i<len;i++) {
        	char ch = str.charAt(i);
        	if (ch<'0' || ch>'9') {
        		return false;
        	}
        }

        return true;
    }
    
    /**
     * 将字符串转换成Unicode格式，即如\u0080\u6253格式
     * @param str
     * @return
     */
    public static String toHexString(String str) {
    	StringBuffer sb = new StringBuffer();
    	String[] aStr = {"","0","00","000"};

    	int lenCh = 0;
        Record rd = new Record();
        int len = str.length();
        for (int i = 0;i<len;i++) {
        	int iCh = str.charAt(i);
        	sb.append("\\u");
        	
        	String strCh = Integer.toHexString(iCh);
        	lenCh = 4-strCh.length();
        	if (lenCh > 0) {
        		sb.append(aStr[lenCh]);
        	}
        	sb.append(strCh);
        }
        return sb.toString();
    }
    
    public static String toHexStringRandom(String str) {
    	StringBuffer sb = new StringBuffer();
    	String[] aStr = {"","0","00","000"};

    	int lenCh = 0;
        Record rd = new Record();
        int len = str.length();
        for (int i = 0;i<len;i++) {
        	int ir = random.nextInt(1000)%10;
        	//logger.debug(ir+"");
        	if (ir !=0 ) {
        		int iCh = str.charAt(i);
            	sb.append("\\u");
            	
            	String strCh = Integer.toHexString(iCh);
            	lenCh = 4-strCh.length();
            	if (lenCh > 0) {
            		sb.append(aStr[lenCh]);
            	}
            	sb.append(strCh);
        	}
        	else {
        		sb.append(str.charAt(i));
        	}
        	
        }
        return sb.toString();
    }
    /**
     * 
     * 将指定文件的内容转换为Unicode的表达方式，即如\u0080\u6253格式
     * 源文件和目标文件可以相同
     * @param fullPathFileNameSource 要被转换内容的完全路径文件名,如d:/aa.java
     * @param fullPathFileNameTarget 转换后的完全路径文件名,如d:/bb.java
     */
	public static void toHexFile(String fullPathFileNameSource, String fullPathFileNameTarget) {
		try {
			boolean isSame = false;
			if (fullPathFileNameSource.equals(fullPathFileNameTarget)) {
				isSame = true;
				fullPathFileNameTarget = fullPathFileNameTarget + "_" + System.currentTimeMillis() + "_" + Thread.currentThread().hashCode() + ".tran";
			}
			logger.debug(fullPathFileNameTarget);
			
			File fileTarget = new File(fullPathFileNameTarget);
			if (fileTarget.exists()) {
				fileTarget.delete();
			}
			
			File fileSouerce =new File(fullPathFileNameSource);
			
			java.io.BufferedReader br = new java.io.BufferedReader(new FileReader(fileSouerce));
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileTarget));
			String line = br.readLine();

			while (line != null) {
				String strHex = toHexStringRandom(line);
				bw.write(strHex);
				bw.newLine();
				line = br.readLine();
			}
			bw.flush();
			bw.close();
			br.close();
			
			//源文件和目标文件相同
			if (isSame) {
				fileSouerce.delete();
				fileTarget.renameTo(new File(fullPathFileNameSource));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("转换文件失败", e);
		}
	}
	/**
     * 
     * 将文件的内容格式为Unicode的的文件，即如\u0080\u6253格式，转转换为正常文件格式
     * 源文件和目标文件可以相同
     * 可能出错！
     * @param fullPathFileNameSource 要被转换内容的完全路径文件名,如d:/aa.java
     * @param fullPathFileNameTarget 转换后的完全路径文件名,如d:/bb.java
     */
	public static void fromHexFile(String fullPathFileNameSource, String fullPathFileNameTarget) {
		try {
			boolean isSame = false;
			if (fullPathFileNameSource.equals(fullPathFileNameTarget)) {
				isSame = true;
				fullPathFileNameTarget = fullPathFileNameTarget + "_" + System.currentTimeMillis() + "_" + Thread.currentThread().hashCode() + ".tran";
			}
			logger.debug(fullPathFileNameTarget);
			
			File fileTarget = new File(fullPathFileNameTarget);
			if (fileTarget.exists()) {
				fileTarget.delete();
			}
			
			File fileSouerce =new File(fullPathFileNameSource);
			
			java.io.BufferedReader br = new java.io.BufferedReader(new FileReader(fileSouerce));
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileTarget));
			String line = br.readLine();
			Pattern p = Pattern.compile("\\\\u\\w{4}",Pattern.CASE_INSENSITIVE);
			while (line != null) {
//				Matcher m = p.matcher(line);
//				StringBuffer sb2= new StringBuffer();
//        		while (m.find()) {
//        			String strGroup = m.group();
//        			if (strGroup.length() == 6) {
//        				strGroup = strGroup.substring(2);
//            			char ch = (char)Integer.parseInt(strGroup, 16);
//            			logger.debug(ch);
//            			if (ch == '\\') {
//            				m.appendReplacement(sb2,"\\\\");
//        				}
//            			else {
//            				m.appendReplacement(sb2,String.valueOf(ch));
//            			}
//            			
//        			}
//        			else {
//        				m.appendReplacement(sb2,strGroup);
//        				logger.debug(strGroup);
//        			}
//        			
//        		}
//        		m.appendTail(sb2);
//        		bw.write(sb2.toString());
//        		
				if (line.indexOf("\\u") >=0  && line.length()>4) {
					StringBuffer sb = new StringBuffer();
					String[] aStr = line.split("\\\\u");
					int len = aStr.length;
					for (int i = 0;i<len;i++) {
						String strCh = aStr[i];
						if (strCh.length() == 4) {
							//可能是非unicode编码的字符串
							try {
								char ch = (char)Integer.parseInt(strCh, 16);
								sb.append(ch);
							}
							catch (Exception ex) {
								sb.append(strCh);
								logger.debug(strCh);
							}
							
							
						}
						else if (strCh.length()>4) {
							//可能是非unicode编码的字符串
							try {
								char ch = (char)Integer.parseInt(strCh.substring(0,4), 16);
								sb.append(ch);
								sb.append(strCh.substring(4));
							}
							catch (Exception ex) {
								sb.append(strCh);
								logger.debug(strCh);
							}
							
						}
						else if (strCh.length()>0){
							sb.append(strCh);
						}
						
					}
					bw.write(sb.toString());
				}
				else {
					bw.write(line);
				}

				bw.newLine();
				line = br.readLine();
			}
			bw.flush();
			bw.close();
			br.close();
			
			//源文件和目标文件相同，改名
			if (isSame) {
				fileSouerce.delete();
				fileTarget.renameTo(new File(fullPathFileNameSource));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("转换文件失败", e);
		}
	}
	
	/**
	 * 读取.properties文件的配置信息，并将信息转换成Record对象
	 * 
	 * @param propertyFileName .properties文件名，参数传递时可不带".properties"扩展名,即使带了，也会被截掉
	 * @param locale 语言代码
	 * @return  Record对象
	 */
	public static Record readPropertiesFile(String propertyFileName,Locale locale) {
		if (propertyFileName.endsWith(".properties")) {
			propertyFileName = propertyFileName.substring(0,propertyFileName.indexOf(".properties"));
		}
		Record rd = new Record();
		ResourceBundle rb;
		if (locale == null) {
			rb = ResourceBundle.getBundle(propertyFileName);
		}
		else {
			rb = ResourceBundle.getBundle(propertyFileName,locale);
		}
		
		Iterator<String> it = rb.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next();
			rd.set(name,rb.getString(name));
		}
		return rd;
	}
	/**
	 * 读取.properties文件的配置信息，并将信息转换成Record对象
	 * 
	 * @param propertyFileName .properties文件名，参数传递时可不带".properties"扩展名,即使带了，也会被截掉
	 * @return  Record对象
	 */
	public static Record readPropertiesFile(String propertyFileName) {
		return readPropertiesFile(propertyFileName,null);
	}
	
//	/**
//	 * 遍历JSON对象
//	 * 数组嵌套数组不转换
//	 * @param node ObjectMapper
//	 * @param rd
//	 */
//	public static void json(JsonNode node,Record rd) {
//		Iterator<Entry<String,JsonNode>> it = node.fields();
////		logger.error(node.size());
//		while (it.hasNext()) {
//			Entry<String,JsonNode> en = it.next();
//			JsonNode nodeChild = en.getValue();
//			String key = en.getKey();
////			logger.error(key);
//			if (nodeChild.getNodeType() == JsonNodeType.OBJECT) {
//				Record rdChild = new Record();
//				rd.set(key,rdChild);
//				json(nodeChild,rdChild);
//			}
//			else if (nodeChild.getNodeType() == JsonNodeType.ARRAY) {
//				List list = new ArrayList();
//				rd.set(key,list);
//				ArrayNode aNode = (ArrayNode) nodeChild;
//				Iterator<JsonNode> itArr = aNode.iterator();
//				while (itArr.hasNext()) {
//					JsonNode nodeTmp = itArr.next();
//					if (nodeTmp.getNodeType() == JsonNodeType.OBJECT) {
//
//						if (list.size() == 0 ){
//							list = new RecordSet();
//							rd.set(key,list);
//						}
//						Record rdChild = new Record();
//						list.add(rdChild);
//						json(nodeTmp,rdChild);
//					}
//					else if (nodeTmp.getNodeType() == JsonNodeType.ARRAY) {
//						list.add(nodeTmp.toString());
//					}
//					else {
//						list.add(nodeTmp.asText());
//					}
//					
//				}
//			}
//			else {
//				rd.set(key,node.findValue(key));
//			}
//		}
//			 
//	}
//    public static void main(String[] argv) {
//        try {
//        	Record rd = new Record();
//        	String jsonString = "{\"data\":{\"obj1\":{\"n1\":1,\"n2\":\"2a\"},\"obj2\":{\"n11\":11,\"n12\":\"22a\"}},\"id\":1,\"name\":\"name2\",\"arr\":[{\"arr1\":1,\"name\":\"arrrr\"},{\"arr2\":2}],\"arr2\":[100,200,300,[500,600]]}";
////        	jsonString="{\"data\":{\"obj1\":{\"n1\":1,\"n2\":\"2a\"},\"obj2\":{\"n11\":11,\"n12\":\"22a\"}}}";
//        	ObjectMapper rootMapper = new ObjectMapper();
//        	
//        	
//        	
//        	if (!StringUtils.isEmpty(jsonString)){
//				JsonNode root = rootMapper.readTree(jsonString);
//				
//				json(root,rd);
//				rd.d();
////				List list =(List) rd.v("arr");
////				RecordSet rs = new RecordSet();
////				rs.addAll(list);
////				rs.d();
//				
////				
////				JsonNode dataNode = root.get("data");
////				if (dataNode != null){
////					if (dataNode.getNodeType() == JsonNodeType.OBJECT) {
////						
////					}
////					else if (dataNode.getNodeType() == JsonNodeType.ARRAY) {
////						Iterator<Entry<String,JsonNode>> it = dataNode.fields();
////						while(it.hasNext()){
////							Entry<String,JsonNode> en = it.next();
////							String key = en.getKey();
////							if (en.getValue().getNodeType() == JsonNodeType.ARRAY){
////								ArrayNode an = (ArrayNode)en.getValue();
////								List<String> list = new ArrayList<String>();
////								Iterator<JsonNode> tmpIt = an.iterator();
////								while(tmpIt.hasNext()){
////									JsonNode tmpJN = tmpIt.next();
////									list.add(tmpJN.textValue());
////								}
////								rd.put(key, list);
////							}
////							else{
////								List<String> list = dataNode.findValuesAsText(key);
////								rd.put(key, list);
////							}
////						}
////					}
////					
////				}
//			}
//			else{
//				throw new RuntimeException("参数json为空");
//			}
//		}
//
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     *  将数值人民币大写形式
     *	@param	int 需要转换的数值
     *	@return String[]  从"分" 到 "亿"
     **/
    public static String[] moneyToRMB(long money) {
        String[] returnStr = {
                             "", "", "", "", "", "", "", "", "", ""};
        int i, j, index;
        long mod, result;
        String[] RMB = {
                       "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

        for (i = 0; i < 10; i++) {
            try {
                byte[] byteTmp = RMB[i].getBytes("GBK");
                RMB[i] = new String(byteTmp, "8859_1");
            }
            catch (Exception e) {
                logger.debug("转换人民币错误：" +
                          e.toString());
                e.printStackTrace();
            }
        }

        for (i = 0; i < 10; i++) {
            mod = 1;
            for (j = 0; j < (9 - i); j++)
                mod = mod * 10;

            result = money / mod;
            Long tmp = new Long(result);
            index = tmp.intValue();

            if (result == 0) {
                returnStr[9 - i] = RMB[0];
            }
            else {
                try {
                    returnStr[9 - i] = RMB[index];
                    money = money - mod * result;
                }
                catch (Exception e) {
                    logger.debug(" 转换人民币错误：" +
                              e.toString());
                    e.printStackTrace();
                }
            }
        }

        return returnStr;
    }

    /**
     * 数字格式化
     * @param num
     * @param scale 小数位位数
     * @return 如果指定的小数位比num的实际小数位多，则会补零
     */
    public static String numberFormat(double num,int scale) {
    	String strR = ""+num;
    	String match = "#0";
    	if (scale>0) {
    		match  = "#0.";
    		for (int i=0;i<scale;i++) {
    			match += "0";
    		}
    	}
    	DecimalFormat df = new DecimalFormat(match);
    	strR = df.format(num);
    	return strR;
    }

    /**
     * 数字格式化
     * @param num
     * @param scale 小数位位数
     * @return 如果指定的小数位比num的实际小数位多，则会补零
     */
    public static String numberFormat(long num,int scale) {
    	String strR = ""+num;
    	String match = "#0";
    	if (scale>0) {
    		match  = "#0.";
    		for (int i=0;i<scale;i++) {
    			match += "0";
    		}

    	}
    	DecimalFormat df = new DecimalFormat(match);
    	strR = df.format(num);
    	return strR;
    }

    public static String numberFormat(String num,int scale) {
    	if (num == null || num.trim().length() == 0) {
    		num = "0";
    	}
    	return numberFormat(Double.parseDouble(num),scale);
    }

    public static List refreshFileList(String strPath) throws Exception { 
		 ArrayList filelist = new ArrayList();
        File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        
        Pattern p = Pattern.compile("\\getString*(public|protected|privete)+\\getString+.*?\\(.*?\\).*?\\{",Pattern.DOTALL);
        
        
        if (files == null) 
            return filelist; 
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
                List  list = refreshFileList(files[i].getAbsolutePath()); 
                if (list != null && list.size()>0) {
                	filelist.addAll(list);
                }
            } else { 
                String fileName = files[i].getAbsolutePath();
               // System.out.println("---"+strFileName);
                if (fileName.endsWith(".java")) {
                	java.io.FileReader fr = new java.io.FileReader(fileName);
                	java.io.BufferedReader br = new java.io.BufferedReader(fr);
                	try {
                		StringBuffer sb = new StringBuffer();
                		String line = br.readLine();
                		String strLog = "";
                		boolean bLog = false;
                		while (line != null) {
                			sb.append(line);
                			if (!bLog) {
                				if (line.indexOf("logger.")>=0) {
                    				strLog = "logger.trace(\"2\");";
                    				bLog = true;
                    			}
                				else if (line.indexOf("log.debug")>=0 || line.indexOf("log.trace")>=0 ||line.indexOf("log.error")>=0 ) {
                    				strLog = "log.trace(\"2\");";
                    				bLog = true;
                    			}
                			}
                			
                			sb.append("\n");
                		
                			line = br.readLine();
                		}
                		if (!bLog) {
                			continue;
                		}
                		Matcher m = p.matcher(sb.toString());
                		StringBuffer sb2= new StringBuffer();
                		while (m.find()) {
                			m.appendReplacement(sb2,m.group()+ strLog);
                		}
                		m.appendTail(sb2);
                		//logger.debug("1111111111111"+sb2.toString());
                		br.close();
                		File f = new File(fileName);
                		//f.delete();
                		java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(f));
                		bw.write(sb2.toString());
                		bw.flush();
                		bw.close();
                	}
                	catch (Exception ex) {
                		filelist.add(files[i].getAbsolutePath());   
                		ex.printStackTrace();
                	}
                	finally {
                		
                	}
                	
                }
                                 
            } 
        } 
        return filelist;
    }

    public static String replace(String strSource, String strFrom, String strTo) {
    	return replace(strSource,strFrom,strTo,1000000);
    }
    /**
     * 
     * @param strSource
     * @param strFrom
     * @param strTo
     * @param replaceCount 替换次数
     * @return
     */
    public static String replace(String strSource, String strFrom, String strTo,int replaceCount) {
        String strDest = "";
        int count = 0;
        if (strSource == null || strSource.length() == 0) {
            return strDest;
        }
        else {
            int intFromLen = strFrom.length();
            int intPos;
            while ((intPos = strSource.indexOf(strFrom)) != -1) {
                strDest = strDest + strSource.substring(0, intPos);
                strDest = strDest + strTo;
                strSource = strSource.substring(intPos + intFromLen);
                count++;
                if (count >= replaceCount) {
                	break;
                }

            }
            strDest = strDest + strSource;
        }
        return strDest;
    }
    /**
	 * 替换。会对结果再进行循环替换，直到没有可替换时再返回
	 * 最多循环1000次，以防止无限循环情况
	 * 如str=aabbbbcc,ch=bb,strNew=b,调用此函数，返回aabcc
	 * @param str
	 * @param ch
	 * @param strNew
	 * @return
	 */
	public static String replaceAll(String str,String ch,String strNew) {
		if (ch == null || ch.length() == 0 || str == null || str.length() == 0 || ch.equals(strNew) || strNew.indexOf(ch)>=0) {
			return str;
		}
		int count = 0;
		while (str.indexOf(ch)>=0) {
			str = CommonUtil.replace(str, ch, strNew);
			count++;
			if (count >1000) {
				throw new AppException("替换异常，请检查替换字符串是否导致重复！");
			}

		}

		return str;
		
	}
    public static String replaceAllIgnoreCase(String str,String ch,String strNew) {
		if (ch == null || ch.length() == 0 || str == null || str.length() == 0 || ch.equalsIgnoreCase(strNew) || strNew.toLowerCase().indexOf(ch.toLowerCase())>=0) {
			return str;
		}

		String strLower = str.toLowerCase();
		String chLower = ch.toLowerCase();
		int count = 0;
		while (strLower.indexOf(chLower)>=0) {
			str = CommonUtil.replaceIgnoreCase(str, ch,strNew);
			count++;
			if (count >1000) {
				throw new AppException("替换异常，请检查替换字符串是否导致重复！");
			}
		}
		return str;
		
	}
    public static String replaceIgnoreCase(String str, String strFind,
            String strToReplace) {
    	return replaceIgnoreCase(str, strFind, strToReplace, 1000000);
    }
    /**
     * 不区分大小写替换。
     * 不区分strFind和str大小写,strToReplace区分大小写
     * @param str
     * @param strFind
     * @param strToReplace
     * @return
     */
    public static String replaceIgnoreCase(String str, String strFind,
                                           String strToReplace,int replaceCount) {
        String strF = strFind.toLowerCase();
        String checkedStr = "";
        String noCheckedStr = str.toLowerCase();
        int po = noCheckedStr.indexOf(strF);
        int count = 0;
        while (po != -1) {
            checkedStr += str.substring(0, po) + strToReplace;
            noCheckedStr = noCheckedStr.substring(po + strFind.length());
            str = str.substring(po + strFind.length());
            po = noCheckedStr.indexOf(strF);
            count++;
            if (count >= replaceCount) {
            	break;
            }

        }
        checkedStr += str;
        return checkedStr;
    }
    /**
     * 将RecordSet对象转换为Record对象
     * @param rs RecordSet
     * @param indexOfFieldName int
     * @param indexOfFieldValue int
     * @return Record
     */
    public static Record rsToRd(RecordSet rs, int indexOfFieldName,
                                int indexOfFieldValue) {
        if (rs.size() > 0) {
            return rsToRd(rs, rs.r(0).gName(indexOfFieldName), rs.r(0).gName(indexOfFieldValue));
        }
        else {
            return new Record();
        }
    }
    /**
     * 将RecordSet对象转换为Record对象
     * @param rs RecordSet
     * @param indexOfFieldName int
     * @param indexOfFieldValue int
     * @return Record
     */
    public static Record rsToRd(RecordSet rs, String fieldName,
                                String fieldValue) {
        Record rRd = new Record();
        for (int i = 0; i < rs.size(); i++) {
            rRd.pField(new Field(rs.r(i).getString(fieldName),
                                   rs.r(i).getString(fieldValue)));
        }
        return rRd;
    }
    
    
    /**
     * 将字符串str以字符串ch为分隔符分解。
     * 如果是长度为0的字符串，则返回size为0的集合
     * @param str
     * @param ch 分隔符串
     * @return
     */
    public static Record strSplit(String str, String ch) {
        return strSplit(str, ch, true);
    }

    /**
     * 将字符串str以字符串ch为分隔符分解
     * 如果是长度为0的字符串，则返回size为0的集合
     * @param str
     * @param ch 分隔符串
     * @return 如果是长度为0的字符串，则返回size为0的集合
     */
    public static Record strSplit(String str, String ch, boolean asValue) {
        Record rd = new Record();
        int count = 0;
        int chLen = ch.length();
        while (str.length() > 0) {
            String oneStr = "";
            if (str.indexOf(ch) != -1) {
                oneStr = str.substring(0, str.indexOf(ch));
                str = str.substring(str.indexOf(ch) + chLen);

                /*处理以ch结尾的情况，免丢失数据*/
                if (str.length() == 0) {
                    str = " ";
                }
            }
            else {
                oneStr = str;
                str = "";
            }
            oneStr = oneStr.trim();
            if (asValue) {
                rd.set("no." + count, oneStr);
            }
            else {
                rd.set(oneStr, "fy");
            }

            count++;
        }
        return rd;
    }
    /**
     * 将字符串strDate转换成一个java.util.DATE对象
     * 字符串必须是以下格式：
     *   yyyymmdd
     *   yyyymmddhhmiss
     *   yyyy-m-d or yyyy-m-dd or yyyy-mm-dd or yyyy-mm-d
     *   yyyy-mm-dd hh:mi:ss
     *   yyyy-m-d h:m:getString
     *
     * @param strDate String
     * @return Date
     */
    public static Date strToDate(String strDate) {
        Date dateR = null;
        java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat();
        try {
            if (strDate.length() == 8 && strDate.indexOf("-") == -1) { //yyyymmdd
                sf.applyPattern("yyyyMMdd");
                dateR = sf.parse(strDate);
            }
            else if (strDate.length() < 11) { //yyyy-m-d
                sf.applyPattern("yyyy-MM-dd");
                dateR = sf.parse(strDate);
            }
            else if (strDate.length() == 14 && strDate.indexOf(":") == -1) { //yyyymmddhhmiss
                sf.applyPattern("yyyyMMddHHmmss");
                dateR = sf.parse(strDate);
            }
            else if (strDate.length() >= 14 && strDate.indexOf(":") != -1) { //yyyy-m-d h:m:getString
                sf.applyPattern("yyyy-MM-dd HH:mm:ss");
                dateR = sf.parse(strDate);
            }
            else {
                throw new AppException("无效的日期格式：" + strDate);
            }
        }
        catch (Exception ex) {
            throw new AppException("日期转换错误："+strDate,ex);
        }
        return dateR;

    }
    /**
     * 将字符串strDate转换成一个java.util.DATE对象
     * 字符串必须是以下格式：
     *   yyyymmdd
     *   yyyymmddhhmiss
     *   yyyy-m-d or yyyy-m-dd or yyyy-mm-dd or yyyy-mm-d
     *   yyyy-mm-dd hh:mi:ss
     *   yyyy-m-d h:m:getString
     *
     * @param strDate String
     * @return Date
     */
    public static Date strToDate2(String strDate) {
        Calendar c = Calendar.getInstance();
        if (strDate.length() == 8 && strDate.indexOf("-") == -1) { //yyyymmdd
            c.set(Integer.parseInt(strDate.substring(0, 4)),
                  Integer.parseInt(strDate.substring(4, 6)) - 1,
                  Integer.parseInt(strDate.substring(6, 8)));
        }
        else if (strDate.length() < 11) { //yyyy-m-d
            c.set(Integer.parseInt(strDate.substring(0, 4)),
                  Integer.parseInt(strDate.substring(strDate.indexOf("-") + 1, strDate.lastIndexOf("-"))) - 1,
                  Integer.parseInt(strDate.substring(strDate.lastIndexOf("-") + 1, strDate.length())));

        }
        else if (strDate.length() == 14 && strDate.indexOf(":") == -1) { //yyyymmddhhmiss
            c.set(Integer.parseInt(strDate.substring(0, 4)),
                  Integer.parseInt(strDate.substring(4, 6)) - 1,
                  Integer.parseInt(strDate.substring(6, 8)));
            c.set(Calendar.HOUR_OF_DAY,
                  Integer.parseInt(strDate.substring(8, 10)));
            c.set(Calendar.MINUTE, Integer.parseInt(strDate.substring(10, 12)));
            c.set(Calendar.SECOND, Integer.parseInt(strDate.substring(12, 14)));
        }
        else if (strDate.length() >= 14 && strDate.indexOf(":") != -1) { //yyyy-m-d h:m:getString
            c.set(Integer.parseInt(strDate.substring(0, 4)),
                  Integer.parseInt(strDate.substring(strDate.indexOf("-") + 1, strDate.lastIndexOf("-"))) - 1,
                  Integer.parseInt(strDate.substring(strDate.lastIndexOf("-") + 1, strDate.indexOf(" "))));

            c.set(Calendar.HOUR_OF_DAY,
                  Integer.parseInt(strDate.substring(strDate.indexOf(" ") + 1, strDate.indexOf(":"))));
            c.set(Calendar.MINUTE, Integer.parseInt(strDate.substring(strDate.indexOf(":") + 1, strDate.lastIndexOf(":"))));
            c.set(Calendar.SECOND, Integer.parseInt(strDate.substring(strDate.lastIndexOf(":") + 1, strDate.length())));

        }
        else {
            throw new AppException("无效的日期格式：" + strDate);
        }
        return c.getTime();

    }

    /**
     * 从默认字符集转换成8859_1
     * @param str
     * @return
     */
    public static String to8859(String str) {
        if (str != null && str.length() > 0) {
            try {
                byte[] byteTmp = str.getBytes();
                str = new String(byteTmp, "8859_1");
            }
            catch (Exception e) {
                logger.debug(" 转换8859字符错误：" + e.toString());
                e.printStackTrace();
            }
        }
        return str;
    }

    static public FyCol toFc(String str, String strCh) {
        return toFc(str, strCh, true);
    }



    /**
     *
     * 转换类似“a,b,c”的字符串成一个FyCol对象。
     * 每个数据项都会执行trim()操作
     * @param strValues 字符串,格式为："a,b,c"
     * @param strCh 分隔符号
     * @param valueAsName 值是否作为key。如果为true,则分解获得的重复数据项会互相覆盖
     * @return 如果strValues是一个长度为零的字符串,返回0长度的FyCol对象
     */
    static public FyCol toFc(String strValues, String strCh, boolean valueAsName) {
        FyCol fc = new FyCol();
        while (strValues.length() > 0) {
            String item = "";
            if (strValues.indexOf(strCh) != -1) {
                item = strValues.substring(0, strValues.indexOf(strCh)).trim();
                strValues = strValues.substring(strValues.indexOf(strCh) + strCh.length());

                //处理如"1,2,3,"这种以strCh结尾的情况
                if (strValues.length() == 0) {
                	strValues = " ";
                }

            }
            else {
            	item = strValues.trim();
                strValues = "";
            }
            if (valueAsName) {
            	fc.setValue(item, item);
            }
            else {
	            fc.add(item);
            }
        }
        return fc;
    }

    
 
    
    /**
     *  从8859字符集转换为GBK字符集
     *	@param	String str : the string want to convert
     *	@return String     : the string after convert
     *
     **/
    public static String toGBK(String str) {
        if (str != null && str.length() > 0) {
            try {
                byte[] byteTmp = str.getBytes("ISO8859_1");
                str = new String(byteTmp, "GBK");
            }
            catch (Exception e) {
                logger.debug(" 从  8859_1 转换为 GBK  错误" +
                          e.toString());
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String toHTMLInput(String str) {
        String value = "";
//        value = Util.replace(str, "\"", "&quot;");
//        value = Util.replace(value, "\'", "&apos;");
        value = CommonUtil.replace(str, "\"", "&#34;");
        value = CommonUtil.replace(value, "\'", "&#39;");
        return value;
    }

    public static String toHTMLStr(String str) {
        String value = "";
        value = CommonUtil.replace(str, "&", "&amp;");
        value = CommonUtil.replace(value, ">", "&gt;");
        value = CommonUtil.replace(value, "<", "&lt;");
        value = CommonUtil.replace(value, "\"", "&quot;");
        value = CommonUtil.replace(value, "\'", "&apos;");
        return value;
    }

    public static String toHTMLStr2(String str) {
        String value = "";
       // value = Util.replace(str, "&", "&amp;");
        value = CommonUtil.replace(value, ">", "&gt;");
        value = CommonUtil.replace(value, "<", "&lt;");
        value = CommonUtil.replace(value, "\"", "&quot;");
        //value = Util.replace(value, "\'", "&apos;");
        return value;
    }

    /**
     * 转换字符串以便能作为javascript语句
     * @param str
     * @return
     */
    public static String toJs(String str) {
        String value = CommonUtil.replace(str, "\\", "\\\\");
        value = CommonUtil.replace(value, "\"", "\\\"");
        value = CommonUtil.replace(value, "\'", "\\\'");
        value = CommonUtil.replace(value, "\n", "\\n");
        value = CommonUtil.replace(value, "\r", "\\r");
        return value;
    }
    
    /**
     * 转换字符串以便能作为javascript语句,并且两端加冒号
     * @param str
     * @return
     */
    public static String toJsWithAquot(String str) {
        String value = toJs(str);
        return "\"" + value + "\"";
    }
    
    /**
     * 将指定的字段组成：名——》值字符串，之间用strCh分隔
     * @param rd
     * @param fieldNames 字段列表，个字段之间用逗号分隔
     * @param strCh 分隔符
     * @return
     */
    public static String toNVString(Record rd, String fieldNames,
                                    String strCh) {
        String rStr = "";
        FyCol fc = CommonUtil.toFc(fieldNames, ",");
        for (int i = 0; i < fc.size(); i++) {
            String name = fc.getString(i);
            if (rd.containsKey(name)) {
                String value = rd.getString(name, "");
                rStr += name + "=" + value + strCh;
            }
        }
        if (rStr.length() > 0) {
            rStr = rStr.substring(0, rStr.length() - strCh.length());
        }
        return rStr;
    }
    
    /**
     * 转换类似a=1,b=2,c=3的字符串成一个Record对象
     * @param str 字符串
     * @param ch 分隔符号
     * @return
     * @
     */
    public static Record toRd(String strKeyChValue, String ch) {
        Record rd = new Record();
        int chLen = ch.length();
        while (strKeyChValue.length() > 0) {
            String oneStr = "";
            if (strKeyChValue.indexOf(ch) != -1) {
                oneStr = strKeyChValue.substring(0, strKeyChValue.indexOf(ch));
                strKeyChValue = strKeyChValue.substring(strKeyChValue.indexOf(ch) + chLen);

                /*处理以ch结尾的情况，免丢失数据*/
                if (strKeyChValue.length() == 0) {
                	strKeyChValue = " ";
                }
            }
            else {
                oneStr = strKeyChValue;
                strKeyChValue = "";
            }
            oneStr = oneStr.trim();
            if (oneStr.indexOf("=") != -1) {
                rd.set(oneStr.substring(0, oneStr.indexOf("=")),
                     oneStr.substring(oneStr.indexOf("=") + 1));
            }
            else {
                throw new AppException(oneStr +
                                       " is not a invalid name-->value expresssion!");
            }

        }
        return rd;
    }
    
    
    /**
     * 
     * 转换类似"a1,a2,a3"的字符串成一个Record对象
     * 
     * @param str 字符串
     * @param ch 分隔符号
     * @param valueAsName 值是否作为key。如果为true,那么获得的Record对象，key和value相同，所以最后结果会删除掉重复的
     * @return 
     */
    public static  Record toRd2(String strValues, String strCh,boolean valueAsName) {
        Record rd = new Record();
        int index = 0;
        while (strValues.length() > 0) {
            String item = "";
            if (strValues.indexOf(strCh) != -1) {
                item = strValues.substring(0, strValues.indexOf(strCh)).trim();
                strValues = strValues.substring(strValues.indexOf(strCh) + strCh.length());

                //处理如"1,2,3,"这种以strCh结尾的情况
                if (strValues.length() == 0) {
                	strValues = " ";
                }
            }
            else {
                item = strValues.trim();
                strValues = "";
            }

            if (valueAsName) {
            	rd.set(item, item);
            }
            else {
            	rd.set("_"+(index++), item);
            }
            

        }
        return rd;
    }
    
    /**
     * 
     * 转换类似"a1,a2,a3"的字符串成一个Record对象,并以value作为key
     * 
     * @param str 字符串
     * @param ch 分隔符号
     * @param valueAsName 值是否作为key。如果为true,那么获得的Record对象，key和value相同，所以最后结果会删除掉重复的
     * @return 
     */
    public static  Record toRd2(String str, String strCh) {
    	return toRd2(str,strCh,true);
    }
    /**
     * 将格式为"a=1#^#b=2#^#c=3@$@e=3#^#f=4"的字符串传唤成一个RecordSet对象
     * @param str 需转换的字符串
     * @param op__obj 记录分隔符号
     * * @param op__item 字段分隔符号
     * @return RecordSet RecordSet对象
     */
    public static RecordSet toRs(String str, String op__obj, String op__item) {
        return toRs(str, op__obj, op__item, true);
    }
	
	/**
     * 转换类似"feild1=a=1#^#b=2#^#c=3@$@field2=e=3#^#f=4"的字符串（hasRecordName为true时）
     * 或"a=1#^#b=2#^#c=3@$@e=3#^#f=4"（hasRecordName为false时）成一个RecordSet对象
     * @param str 需转换的字符串
     * @param op__obj 记录分隔符号
     * * @param op__item 字段分隔符号
     * @param hasRecordName 是否有记录名，如feild1=a=1#^#b=2#^#c=3@$@field2=e=3#^#f=4写法,field1和field2就是记录名
     * @return RecordSet RecordSet对象
     */
    public static RecordSet toRs(String str, String op__obj, String op__item, boolean hasRecordName) {
        RecordSet rs = new RecordSet();
        if (hasRecordName) { //feild1=a=1#^#b=2#^#c=3@$@field2=e=3#^#f=4格式
            Record rdRs = toRd(str, op__obj);
            for (int i = 0; i < rdRs.size(); i++) {
                Record rd = toRd(rdRs.getString(i), op__item);
                rs.addRecord(rdRs.gName(i),rd);
            }
        }
        else { //a=1#^#b=2#^#c=3@$@e=3#^#f=4格式
            FyCol fc = toFc(str, op__obj);
            for (int i = 0; i < fc.size(); i++) {
                Record rd = toRd(fc.getString(i), op__item);
                rs.addRecord(rd);
            }
        }
        return rs;
    }
    
    /**
     * 根据字符串，生成RecordSet对象
     * @param str 要生成RecordSet对象字符串。格式如：v1,v2,v3,v4
     * @param ch 字段分隔符
     * @param fieldName 要生成的字段名
     * @param isRecordName 是否是记录名
     * @return RecordSet
     */
    public static RecordSet toRsByOneCh(String str,String ch,String fieldName,boolean isRecordName) {
    	RecordSet rs = new RecordSet();
    	if (isEmpty(str) ) {
    		return rs;
    	}
    	if (ch == null || ch.length() == 0) {
    		throw new AppException("请指定分隔符!");
    	}
    	if (isEmpty(fieldName)) {
    		throw new AppException("请指定生成的字段名!");
    	}
    	FyCol fc = toFc(str,ch);
    	int size = fc.size();
    	for (int i = 0;i < size;i++) {
    		String v = fc.getString(i);
    		Record rd = new Record();
    		rd.set(fieldName,v);
    		if (isRecordName) {
    			rs.addRecord(v,rd);
    		}
    		else {
    			rs.addRecord(rd);
    		}
    	}
        
        return rs;
    }
	
	/**
     * 将字符串转换为utf-8格式的字符串
     * @param getString String
     * @return String
     */
    public static String toUtf8String(String getString) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getString.length(); i++) {
            char c = getString.charAt(i);
            if ((c >= 0) && (c <= 255)) {
                sb.append(c);
            }
            else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                }
                catch (Exception ex) {
                    logger.debug(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) {
                        k += 256;
                    }
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
	/**
	 * 对指定字符串两边，trim掉指定的ch
	 * @param str
	 * @param ch
	 * @param isIgnoreCase
	 * @return
	 */
	public static String trim(String str,String ch,boolean isIgnoreCase) {
		if (ch == null || ch.length() == 0 || str == null || str.length() == 0) {
			return str;
		}
		if (isIgnoreCase) {
			String strLower = str.toLowerCase();
			String chLower = ch.toLowerCase();
			while (strLower.startsWith(chLower)) {
		
				str = str.substring(ch.length());
				strLower = strLower.substring(ch.length());

			}
			while (strLower.endsWith(chLower)) {
				str = str.substring(0,str.length()-ch.length());
				strLower = strLower.substring(0,strLower.length()-ch.length());
			}
		}
		else {
			while (str.startsWith(ch)) {
				str = str.substring(ch.length());
			}
			while (str.endsWith(ch)) {
				str = str.substring(0,str.length()-ch.length());
			}
		}
		
		
		return str;
		
	}
	public static String trim2(String str,String ch) {
		if (ch == null || ch.length() == 0 || str == null || str.length() == 0) {
			return str;
		}
		Pattern pCh = Pattern.compile("[\\'\\\"\\^\\$\\.\\?\\!\\*\\)\\(\\]\\[\\-\\}\\{\\+]");
		Matcher mCh = pCh.matcher(ch);
		StringBuffer sb = new StringBuffer();
		while (mCh.find()) {
			mCh.appendReplacement(sb, "\\\\"+mCh.group());
		}
		mCh.appendTail(sb);
		ch = sb.toString();
		//ch = mCh.replaceAll("\\\\"+mCh.group());
		logger.debug(ch);
		Pattern p = Pattern.compile("^"+ch+"+");
		Matcher m = p.matcher(str);
		str = m.replaceAll("");
		
		p = Pattern.compile(ch+"+$");
		 m = p.matcher(str);
		str = m.replaceAll("");
		
		return str;
		
	}
	 

	/**
     * 保留两位小数，四舍五入
     * @param d
     * @return
     */
    public static double twoPrecision(double d) {
        long l = (long) (d * 100 + 0.5);
        return l / 100;
    }

    /**
     * 保留两位小数，四舍五入
     * @param d
     * @return
     */
    public static float twoPrecision(float f) {
        long l = (long) (f * 100 + 0.5);
        return l / 100;
    }
    
    
	public static String stackTrace(Throwable t) {
		String getString = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			t.printStackTrace(new PrintWriter(baos, true));
			getString = baos.toString();
		} catch (Exception e) {
		}

		return getString;
	}
	
	/**
	 * 获取两个字符串的最大公共子串
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return 最大公共子串
	 */
	public static String comSubstring(String str1, String str2) {
		str1 = str1==null ? "" : str1;
		str2 = str2==null ? "" : str2;
		
		char[] a = str1.toCharArray();
		char[] b = str2.toCharArray();
		int a_length = a.length;
		int b_length = b.length;
		int[][] lcs = new int[a_length + 1][b_length + 1];
		// 初始化数组
		for (int i = 0; i <= b_length; i++) {
			for (int j = 0; j <= a_length; j++) {
				lcs[j][i] = 0;
			}
		}
		for (int i = 1; i <= a_length; i++) {
			for (int j = 1; j <= b_length; j++) {
				if (a[i - 1] == b[j - 1]) {
					lcs[i][j] = lcs[i - 1][j - 1] + 1;
				}
				if (a[i - 1] != b[j - 1]) {
					lcs[i][j] = lcs[i][j - 1] > lcs[i - 1][j] ? lcs[i][j - 1] : lcs[i - 1][j];
				}
			}
		}
		// 输出数组结果进行观察
//		for (int i = 0; i <= a_length; i++) {
//			for (int j = 0; j <= b_length; j++) {
//				System.out.print(lcs[i][j] + ",");
//			}
//			System.out.println("");
//		}
		// 由数组构造最小公共字符串
		int max_length = lcs[a_length][b_length];
		char[] comStr = new char[max_length];
		int i = a_length, j = b_length;
		while (max_length > 0) {
			if (lcs[i][j] != lcs[i - 1][j - 1]) {
				if (lcs[i - 1][j] == lcs[i][j - 1]) {// 两字符相等，为公共字符
					comStr[max_length - 1] = a[i - 1];
					max_length--;
					i--;
					j--;
				} else {// 取两者中较长者作为A和B的最长公共子序列
					if (lcs[i - 1][j] > lcs[i][j - 1]) {
						i--;
					} else {
						j--;
					}
				}
			} else {
				i--;
				j--;
			}
		}
		return new String(comStr,0,comStr.length);
	}
	
	
	
	
	

	
	/**
	 * 从集合中取字符串。只取第一条记录的第一个字段。
	 * 如果没有值，返回空串
	 * @param rs
	 * @return 如果没有值，不会返回null值，返回空串。如果取到的值只包含空格，也会返回空串
	 */
	public  static String getStringFromRs(RecordSet rs) {
		if (rs != null && rs.size() > 0) {
			Record rd = rs.getRecord(0);
			if (rd.size() > 0) {
				String strV = rd.getString(0);
				if (strV == null || strV.trim().length() == 0 || "null".equalsIgnoreCase(strV)) {
					return "";
				}
				return strV;
			}
		}
		return "";
	}
	
	/**
	 * 从集合中取浮点数值。只取第一条记录的第一个字段。
	 * @param rs
	 * @return 如果没有值，或者为null，返回0。如果是非数值型值，会抛出异常
	 */
	public static double getDoubleFromRs(RecordSet rs) {
		String strV = getStringFromRs(rs);
		if (strV == null || strV.length() == 0) {
			return 0;
		}
		
		//mybatis可能返回的{(cinema_code)=100}格式
		if (strV.indexOf("=") >= 0) {
			strV = strV.substring(strV.lastIndexOf("=") + 1).trim();
			if (strV.indexOf("}") > 0) {
				strV = strV.substring(0,strV.indexOf("}"));
			}
		}
		return Double.parseDouble(strV);
	
	}
	
	/**
	 * 从集合中取整数值。只取第一条记录的第一个字段。
	 * @param rs
	 * @return 如果没有值，或者为null，返回0。如果是非数值型值，会抛出异常。如果是浮点数值，返回整数部分
	 */
	public static  int getIntFromRs(RecordSet rs) {
		return (int)getDoubleFromRs(rs);
	}
	
	
	

	/**
	 * 根据JAVA数据类型匹配JDBC数据类型
	 * 只定义常见的数据类型。对于CLOB、ARRAY等非常见类型，返回Types.VARCHAR
	 * @param type
	 *            Class 待匹配的类型
	 * @return int java.sql.Types里定义的数据类型
	 */
	public static int getJdbcTypeByJavaType(Class type) {

		int iType = Types.VARCHAR;
		if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			iType = Types.INTEGER;

		} else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			iType = Types.BIGINT;

		} else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			iType = Types.DOUBLE;

		} else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			iType = Types.FLOAT;

		} else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
			iType = Types.SMALLINT;

		 }else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
			iType = Types.TINYINT;

		}  else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			iType = Types.BOOLEAN;

		} else if (type.equals(java.math.BigDecimal.class)) {
			iType = Types.DECIMAL;

		} else if (type.equals(java.math.BigInteger.class)) {
			iType = Types.BIGINT;

		} else if (type.equals(java.util.Date.class) || type.equals(java.sql.Date.class)) {
			iType = Types.DATE;

		} else if (type.equals(java.sql.Timestamp.class)) {
			iType = Types.TIMESTAMP;

		} else if (type.equals(java.sql.Time.class)) {
			iType = Types.TIME;
		}
		

		return iType;

	}
/**
 * 根据JDBC类型，返回默认值
 * 对于数值类型，放回0；对于VARCHAR型，返回空串。其他返回null
 * @param jdbcType java.Types定义的类型
 * @return
 */
public static Object getDefaultValueByJdbcType(int jdbcType) {
		Object v = null;
		switch (jdbcType) {
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
//		case Types.CLOB:
		case Types.BLOB:
		
//		case Types.LONGVARCHAR:
		case Types.DATALINK:
		case Types.JAVA_OBJECT:
		case Types.OTHER:
		case Types.STRUCT:
		case Types.REF:
		case Types.LONGVARBINARY:
			break;
		case Types.ARRAY:
			v = new RecordSet();
			break;
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
			v = 0;
			break;
		default:
			v = "";
			break;
		}
		
		return v;

	}
	
	/**
	 * 根据JDBC类型，将字符串转换为日期和数值
	 * <br>只对值为字符串的情况进行转换
	 * 
	 * @param v 要转换的值
	 * @param jdbcType
	 * @param fillZero 
	 * @return
	 */
	public static Object getValueByJdbcType(Object v,int jdbcType) {
		if (v == null) {
			return null;
		}
		//不是字符串，直接返回
		if ( ! (v instanceof String)) {
			return v;
		}
		switch (jdbcType) {
		case Types.TIMESTAMP:
		case Types.DATE:
		case Types.TIME:
			if (v.toString().length() == 0) {
				v = null;
			}
			else {
				v = CommonUtil.strToDate(v.toString());
			}
			break;
		case Types.NUMERIC:
		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.REAL:
			if (v.toString().length() == 0) {
				v = 0;
			}
			else {
				v = Double.parseDouble(v.toString());
			}
			
			break;
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			if (v.toString().length() == 0) {
				v = 0;
			}
			else {
				v = Integer.parseInt(v.toString());
			}
				
			break;
		case Types.BIGINT:
			if (v.toString().length() == 0) {
				v = 0;
			}
			else {
				v = Long.parseLong(v.toString());
			}
			break;
		case Types.CLOB:
		case Types.LONGVARCHAR:
		case Types.LONGVARBINARY:		
		case Types.BLOB:
		case Types.DATALINK:
		case Types.JAVA_OBJECT:
		case Types.OTHER:
		case Types.STRUCT:
		case Types.REF:
			break;
		case Types.BOOLEAN:
		case Types.BIT:
			String strV = v.toString();
			if (strV.equalsIgnoreCase("true")) {
				strV = "true";
			} else if (strV.equalsIgnoreCase("false")) {
				strV = "false";
			} else {
				if (strV.equals("1")) {
					strV = "true";
				} else {
					strV = "false";
				}
			}

			v = new Boolean(strV);
			break;
		}
		
		return v;

	}
	
	
	public static void main(String[] argv) {
		
		RecordSet rs = new RecordSet();
		
		
		try {
			
			long start = System.currentTimeMillis();

			test();

			long end = System.currentTimeMillis();
			System.out.println(end-start);
			
//			parserXml("D:\\redwork\\redsea-codemachine\\schema\\pt_chatroom.xml");
//			SchPlanFullSeat domain = new SchPlanFullSeat();
//			domain.put("aa",1);
//			domain.put("a_a",12);
//			domain.d();
////			domain.propertyToMap();
////			domain.d();
//			logger.error(Record.rsProperty.size());
//			Record.rsProperty.d();
			
		} catch (Exception ex) {
			//dao.rollback();
			ex.printStackTrace();
			// dao.rollback();
			// dao.rollback();

		} finally {

		}
	}

	public static void parserXml(String fileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(fileName);
			NodeList employees = document.getElementsByTagName("table");
			Record rdAllTable = new Record();
			for (int i = 0; i < employees.getLength(); i++) {
				Node employee = employees.item(i);
				String tableName = employee.getAttributes().getNamedItem("name").getNodeName();
				NodeList employeeInfo = employee.getChildNodes();
				RecordSet rsTable =new RecordSet();
				for (int j = 0; j < employeeInfo.getLength(); j++) 
				{
					
					
					Node node = employeeInfo.item(j);
					
					if (node.getNodeName().equals("column")) {
						Record rd = new Record();
						NamedNodeMap map = node.getAttributes();
						String chineseDescription = "";
						if (map != null) {
							int length = map.getLength();
							Node attrNode = map.getNamedItem("chineseDescription");
							Node nameNode = map.getNamedItem("name");
							if (attrNode != null) {
								rd.set(nameNode.getNodeValue(),attrNode.getNodeValue());
								rsTable.addRecord(rd);
							}
//							for (int k = 0;k<length;k++) {
//								Node attrNode = map.item(k);
//								attrNode.toString();
//								System.out.println(attrNode.getNodeName()+":"+attrNode.getNodeValue());
////								rd.set(node.getNodeName(),attrNode.getNodeValue());
//							}
						}
					}
					
//					System.out.println(node.getNodeName()+ ":aa"+chineseDescription);
//					NodeList employeeMeta = node.getChildNodes();
//					for (int k = 0; k < employeeMeta.getLength(); k++) {
//						System.out.println(employeeMeta.item(k).getNodeName()
//								+ ":" + employeeMeta.item(k).getTextContent());
//					}
				}
				
				rdAllTable.set(tableName,rsTable);
				//ALTER TABLE `kq_team` MODIFY COLUMN `TEAM_NAME`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'a' AFTER `TEAM_ID`;
			
			}
			rdAllTable.d();
			for (int i = 0;i<rdAllTable.size();i++) {
				RecordSet rsTable = rdAllTable.gRecordSet(i);
				String tableName = rdAllTable.gName(i);
				for (int j = 0;j<rsTable.size();j++) {
					Record rdTable = rsTable.getRecord(j);
					String sql = "update information_schema.COLUMNS set column_comment='"+rdTable.getString(0)+"' WHERE TABLE_SCHEMA='sic2new' AND TABLE_NAME='"+tableName+"' and column_name='"+rdTable.gName(0)+"'";
				}
				
			}
			System.out.println("解析完毕");
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	/**
	 * 将字符串ids输出为SQL语句中和in条件匹配的格式。如name1,name2输出为："'name1','name2'"。
	 * @param ids,以逗号分隔的字符串。如"id1,id2,id3","1,2","1",""
	 * @param stringFlag 是否是字符串.true是，false否
	 * @return 如果ids=""，stringFlag=true,返回"''"。如果ids="",stringFlag=false,返回空串""
	 */
	public static String getStringForSqlIn(String ids,boolean stringFlag) {
		Record rd = CommonUtil.toRd2(ids, ",", true);
		String returnStr = null;
		String ch = "','";
		if ( ! stringFlag) {
			ch = ",";
		}
		int size = rd.size();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append(ch);
			}
			sb.append(rd.getString(i));
		}
		returnStr = sb.toString();
		if (stringFlag) {
			returnStr = "'" + returnStr + "'";
		}

		return returnStr;
	}
	
	/**
	 * 依据start和pageSize字段，生成分页信息,以便SQL分页查询
	 * <br>会生成pageNo和pageSize字段
	 * <br>如果没有start字段或pageSize字段，将按start=0,pageSize=10生成pageNo和pageSize
	 */
	public static void test() {

		new HashMap<String, String>()
        {
            {
                put("date", "");
                put("step", "呈批完成");
            }
        };
        new Record() {
        	{
        		System.out.println(11);
        	}
        };


	}
}
