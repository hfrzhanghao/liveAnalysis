package com.db.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ConfigToJson;
import com.creator.ICreator;
import com.creator.impl.BrowserVersionCreator;
import com.creator.impl.CityCreator;
import com.creator.impl.DeviceCreator;
import com.creator.impl.DeviceNameCreator;
import com.creator.impl.DurationCreator;
import com.creator.impl.LockCountCreator;
import com.creator.impl.OSCreator;
import com.creator.impl.OpenTypeCreator;
import com.creator.impl.PicDurationCreator;
import com.creator.impl.ProvinceCreator;
import com.creator.impl.SDKCreator;
import com.creator.impl.UserCLCreator;
import com.creator.impl.UserLeaveCreator;
import com.creator.impl.UserOnlineCreator;
import com.creatorPretreat.ICreatorPretreat;
import com.creatorPretreat.impl.BrowserVersionCreatorPretreat;
import com.creatorPretreat.impl.CityCreatorPretreat;
import com.creatorPretreat.impl.DeviceCreatorPretreat;
import com.creatorPretreat.impl.DeviceNameCreatorPretreat;
import com.creatorPretreat.impl.DurationCreatorPretreat;
import com.creatorPretreat.impl.LockCountCreatorPretreat;
import com.creatorPretreat.impl.OSCreatorPretreat;
import com.creatorPretreat.impl.OpenTypeCreatorPretreat;
import com.creatorPretreat.impl.PicDurationCreatorPretreat;
import com.creatorPretreat.impl.ProvinceCreatorPretreat;
import com.creatorPretreat.impl.SDKCreatorPretreat;
import com.creatorPretreat.impl.UserCLCreatorPretreat;
import com.creatorPretreat.impl.UserLeaveCreatorPretreat;
import com.creatorPretreat.impl.UserOnlineCreatorPretreat;
import com.db.business.AllLiveService;
import com.db.business.AllLiveWithPreService;
import com.db.dao.LiveInfoDao;
import com.db.dao.impl.LiveInfoDaoImpl;
import com.db.dao.impl.PlayThread;
//import com.db.entity.LiveInfoEntity;
import com.db.entity.PlayDataEntity;
import com.dto.AllLiveDto;
//import com.dto.AllLogDto;
//import com.dto.LiveListDto;
import com.dto.PlayListDto;
import com.dto.PopularDto;
import com.dto.StatRowDto;
import com.dto.UserDto;
import com.external.common.CommonConstants;

public class AllLiveServiceWithPreImpl implements AllLiveWithPreService {
	private Logger logger = Logger.getLogger(this.getClass());
	private LiveInfoDao liveInfoDao = null;
	final String HAS_NO_LOADPLAYER_EVENT = CommonConstants.HAS_NO_LOADPLAYER_EVENT;

	private ICreatorPretreat deviceCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat deviceNameCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat lockCountCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat durationCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat picDurationCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat provinceCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat cityCreatorPretreat;
	private ICreatorPretreat osCreatorPretreat;// 按设备类型进行统计
	private ICreatorPretreat sdkCreatorPretreat;
	private ICreatorPretreat userCLCreatorPretreat;
	private ICreatorPretreat userLeaveCreatorPretreat;
	private ICreatorPretreat userOnlineCreatorPretreat;
	private ICreatorPretreat browserVersionCreatorPretreat;
	private ICreatorPretreat openTypeCreatorPretreat;

	public AllLiveServiceWithPreImpl(long start, long end) {
		liveInfoDao = new LiveInfoDaoImpl();
		JSONObject json = ConfigToJson.getJSONObject();

		deviceCreatorPretreat = new DeviceCreatorPretreat();
		deviceCreatorPretreat.init(json, null);

		deviceNameCreatorPretreat = new DeviceNameCreatorPretreat();
		deviceNameCreatorPretreat.init(json, null);

		osCreatorPretreat = new OSCreatorPretreat();
		osCreatorPretreat.init(json, null);

		lockCountCreatorPretreat = new LockCountCreatorPretreat();
		lockCountCreatorPretreat.init(json, null);

		durationCreatorPretreat = new DurationCreatorPretreat();
		durationCreatorPretreat.init(json, null);

		picDurationCreatorPretreat = new PicDurationCreatorPretreat();
		picDurationCreatorPretreat.init(json, null);

		provinceCreatorPretreat = new ProvinceCreatorPretreat();
		provinceCreatorPretreat.init(json, null);

		cityCreatorPretreat = new CityCreatorPretreat();
		cityCreatorPretreat.init(json, null);

		sdkCreatorPretreat = new SDKCreatorPretreat();
		sdkCreatorPretreat.init(json, null);

		browserVersionCreatorPretreat = new BrowserVersionCreatorPretreat();
		browserVersionCreatorPretreat.init(json, null);

		userCLCreatorPretreat = new UserCLCreatorPretreat(start, end);
		userCLCreatorPretreat.init(json, null);

		userLeaveCreatorPretreat = new UserLeaveCreatorPretreat(start, end);
		userLeaveCreatorPretreat.init(json, null);

		userOnlineCreatorPretreat = new UserOnlineCreatorPretreat(start, end);
		userOnlineCreatorPretreat.init(json, null);

		openTypeCreatorPretreat = new OpenTypeCreatorPretreat();
		openTypeCreatorPretreat.init(json, null);
	}

	@Override
	public AllLiveDto getDataWithPretreat(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID,
			String userName, String service, int pageSize) {

		long curr = System.currentTimeMillis();
		long monthMillis = 30 * 24 * 60 * 60000L;

		// 如果结束时间大于当前时间，开始时间小于当前时间，则将当前时间赋给结束时间
		if (startTime < curr && endTime > curr) {
			endTime = curr;
		}

		// 若结束时间比开始时间多一个月，则只取时间范围内最近一个月
		if (endTime - startTime > monthMillis) {
			startTime = endTime - monthMillis;
		}

		List<JSONObject> liveListOnline = new ArrayList<JSONObject>();
		List<JSONObject> liveListDto = new ArrayList<JSONObject>();
		
		if(endTime - startTime > 20*60000){
			long startTime_online = System.currentTimeMillis();

			OnlineThread onlineThread = new OnlineThread(startTime, endTime, url, pageSize, 1, true);
			PredataThread predataThread = new PredataThread(startTime, endTime, url, domain, isp, openType, businessID,pageSize, 1, true);
			Thread onlineT = new Thread(onlineThread);
			Thread preT = new Thread(predataThread);

			if (service.equals("all")) {
				onlineT.start();
				preT.start();
				while (!onlineThread.complete || !predataThread.complete) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						logger.error("统计中getDataWithPretreat: ", e);
						System.exit(0);// 退出程序
					}
				}
			} else if (service.equals("userCome") || service.equals("userLeave") || service.equals("userOnline")) {
				onlineT.start();
				while (!onlineThread.complete) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						logger.error("统计中getDataWithPretreat: ", e);
						System.exit(0);// 退出程序
					}
				}
			} else {
				preT.start();
				while (!predataThread.complete) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						logger.error("统计中getDataWithPretreat: ", e);
						System.exit(0);// 退出程序
					}
				}
			}
			liveListOnline = onlineThread.onlineObjList;
			liveListDto = predataThread.preObjList;
			long endTime_online = System.currentTimeMillis();
			System.out.println("daoonline:" + (endTime_online - startTime_online));
		}else{
			liveListDto = liveInfoDao.findLiveWithPretreat(startTime, endTime, url, domain, isp, openType, businessID, userName, pageSize, 1, true);
			liveListOnline = liveInfoDao.findOnlineWithPretreat(startTime, endTime, url, pageSize, 1, true);
		}
		

		/*long startTime_dao_pre = System.currentTimeMillis();
		liveListDto = liveInfoDao.findLiveWithPretreat(startTime, endTime, url, domain, isp, openType, businessID, userName, pageSize, 1, true);
		long endTime_pre = System.currentTimeMillis();
		System.out.println("daopre:" + (endTime_pre - startTime_dao_pre));

		while (!onlineThread.complete) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.error("统计中getDataWithPretreat: ", e);
				System.exit(0);// 退出程序
			}
		}*/

		

		int totalData = liveListDto.size();
		if (totalData >= 0) {
			long startTime_insert = System.currentTimeMillis();
			insertCountWithPretreat(liveListDto, liveListOnline, service, startTime, endTime, domain);
			long endTime_insert = System.currentTimeMillis();
			System.out.println("insertcount:" + (endTime_insert - startTime_insert));
			logger.info("insertcount:" + (endTime_insert - startTime_insert));
			//TODO
			Runtime run = Runtime.getRuntime();
			long startMem = run.totalMemory()-run.freeMemory();
			System.gc();
			long endMem = run.totalMemory()-run.freeMemory();
			System.out.println("memory difference:" + (endMem-startMem));

			return putdata(2, totalData, service, domain);
		}
		return null;
	}

	private void insertCountWithPretreat(List<JSONObject> liveListDto, List<JSONObject> liveListOnline, String service, long startTime, long endTime,
			String domain) {

		// 静态数据
		if (liveListDto.size() > 0) {
			for (JSONObject liveInfo : liveListDto) {
				if (service.equals("domain") || service.equals("all")) {
					try {
						if (domain == null) {
							provinceCreatorPretreat.insertRecord(liveInfo);
						} else {
							cityCreatorPretreat.insertRecord(liveInfo);
						}

					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				// service = "browserVersion";
				if (service.equals("browserVersion") || service.equals("all")) {
					try {
						browserVersionCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("OSType") || service.equals("all")) {
					try {
						deviceCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("deviceName") || service.equals("all")) {
					try {
						deviceNameCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("OSVersion") || service.equals("all")) {
					try {
						osCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("openType") || service.equals("all")) {
					try {
						openTypeCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("playerSDK") || service.equals("all")) {
					try {
						sdkCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("duration") || service.equals("all")) {
					try {
						durationCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("lockCount") || service.equals("all")) {
					try {
						lockCountCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("firstPicDuration") || service.equals("all")) {
					try {
						picDurationCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

			}
		}

		// 动态数据
		if (liveListOnline.size() > 0) {
			for (JSONObject liveInfo : liveListOnline) {
				// service = "userOnline";
				if (service.equals("userOnline") || service.equals("all")) {
					try {
						userOnlineCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("insertRecord:", e);
					}
				}

				// service = "userCome";
				if (service.equals("userCome") || service.equals("all")) {
					try {
						userCLCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				// service = "userLeave";
				if (service.equals("userLeave") || service.equals("all")) {
					try {
						userLeaveCreatorPretreat.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
			}
		}
	}

	private AllLiveDto putdata(int flag, int totalData, String service, String domain) {
		AllLiveDto alldata = new AllLiveDto();

		if (service.equals("browserVersion") || service.equals("all")) {
			StatRowDto browser = new StatRowDto();
			browser.setDescription("browserVersion");
			browser.setRows(browserVersionCreatorPretreat.getRowList());

			alldata.setBrowserVersion(browser);
		}

		if (service.equals("openType") || service.equals("all")) {
			StatRowDto openType = new StatRowDto();
			openType.setDescription("openType");
			openType.setRows(openTypeCreatorPretreat.getRowList());

			alldata.setOpenType(openType);
		}

		if (service.equals("playerSDK") || service.equals("all")) {
			StatRowDto sdk = new StatRowDto();
			sdk.setDescription("playerSDK");
			sdk.setRows(sdkCreatorPretreat.getRowList());

			alldata.setPlayerSDK(sdk);
		}

		if (service.equals("domain") || service.equals("all")) {
			if (domain == null) {
				StatRowDto province = new StatRowDto();
				province.setDescription("province");
				province.setRows(provinceCreatorPretreat.getRowList());

				alldata.setProvince(province);
			} else {
				StatRowDto city = new StatRowDto();
				city.setDescription("city");
				city.setRows(cityCreatorPretreat.getRowList());

				alldata.setCity(city);
			}
		}

		if (service.equals("firstPicDuration") || service.equals("all")) {
			StatRowDto firstPicDuration = new StatRowDto();
			firstPicDuration.setDescription("firstPicDuration");
			firstPicDuration.setRows(picDurationCreatorPretreat.getRowList());

			alldata.setFirstPicDuration(firstPicDuration);
		}

		if (service.equals("duration") || service.equals("all")) {
			StatRowDto duration = new StatRowDto();
			duration.setDescription("duration");
			duration.setRows(durationCreatorPretreat.getRowList());

			alldata.setDuration(duration);
		}

		if (service.equals("lockCount") || service.equals("all")) {
			StatRowDto lockCount = new StatRowDto();
			lockCount.setDescription("lockCount");
			lockCount.setRows(lockCountCreatorPretreat.getRowList());

			alldata.setLockCount(lockCount);
		}

		if (service.equals("OSVersion") || service.equals("all")) {
			StatRowDto OSVersion = new StatRowDto();
			OSVersion.setDescription("osVersion");
			OSVersion.setRows(osCreatorPretreat.getRowList());

			alldata.setOsVersion(OSVersion);
		}

		if (service.equals("deviceName") || service.equals("all")) {
			StatRowDto deviceName = new StatRowDto();
			deviceName.setDescription("deviceName");
			deviceName.setRows(deviceNameCreatorPretreat.getRowList());

			alldata.setDeviceName(deviceName);
		}

		if (service.equals("OSType") || service.equals("all")) {
			StatRowDto OSType = new StatRowDto();
			OSType.setDescription("osType");
			OSType.setRows(deviceCreatorPretreat.getRowList());

			alldata.setOsType(OSType);
		}

		if (service.equals("userCome") || service.equals("all")) {
			StatRowDto userCome = new StatRowDto();
			userCome.setDescription("userCome");
			userCome.setRows(userCLCreatorPretreat.getRowList());

			alldata.setUserCome(userCome);
		}

		if (service.equals("userLeave") || service.equals("all")) {
			StatRowDto userLeave = new StatRowDto();
			userLeave.setDescription("userLeave");
			userLeave.setRows(userLeaveCreatorPretreat.getRowList());

			alldata.setUserLeave(userLeave);
		}

		if (service.equals("userOnline") || service.equals("all")) {
			StatRowDto userOnline = new StatRowDto();
			userOnline.setDescription("userOnline");
			userOnline.setRows(userOnlineCreatorPretreat.getRowList());

			alldata.setUserOnline(userOnline);
		}

		alldata.setTotalCount(new Integer(totalData));

		return alldata;

	}

	@Override
	public List<PopularDto> getPopular(long startTime, long endTime, String domainName, String topN, int pageSize) {
		List<JSONObject> liveList = liveInfoDao.findLivePopular(startTime, endTime, domainName, topN, pageSize, 1, true);
		List<PopularDto> liveListPopular = new ArrayList<PopularDto>();
		Map<String, Integer> popularCount = new HashMap<String, Integer>();
		for (JSONObject liveListObject : liveList) {
			String url = liveListObject.getString("url_key");
			if (url.startsWith("http://" + domainName) || url.startsWith("https://" + domainName)) {
				String province = liveListObject.getString("province_key");
				if (!popularCount.containsKey(url)) {
					popularCount.put(url, liveListObject.getJSONObject("province").getInt(province));
				} else {
					int currentCount = popularCount.get(url);
					popularCount.put(url, currentCount + liveListObject.getJSONObject("province").getInt(province));
				}
			}
		}

		for (String urlKey : popularCount.keySet()) {
			PopularDto popularDto = new PopularDto();
			popularDto.setContent(urlKey);
			popularDto.setCount(popularCount.get(urlKey));
			liveListPopular.add(popularDto);
		}

		Collections.sort(liveListPopular, new Comparator<PopularDto>() {
			@Override
			public int compare(PopularDto o1, PopularDto o2) {
				return o2.getCount() - o1.getCount();
			}
		});

		return liveListPopular.subList(0, Integer.parseInt(topN));
	}
}
