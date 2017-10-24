package com.db.business.impl;

import java.util.ArrayList;
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
import com.db.business.AllLiveService;
import com.db.dao.LiveInfoDao;
import com.db.dao.impl.LiveInfoDaoImpl;
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

public class AllLiveServiceImpl implements AllLiveService {
	private Logger logger = Logger.getLogger(this.getClass());
	private LiveInfoDao liveInfoDao = null;
	final String HAS_NO_LOADPLAYER_EVENT = CommonConstants.HAS_NO_LOADPLAYER_EVENT;

	private ICreator deviceCreator;// 按设备类型进行统计
	private ICreator deviceNameCreator;// 按设备类型进行统计
	private ICreator lockCountCreator;// 按设备类型进行统计
	private ICreator durationCreator;// 按设备类型进行统计
	private ICreator picDurationCreator;// 按设备类型进行统计
	private ICreator provinceCreator;// 按设备类型进行统计
	private ICreator cityCreator;
	private ICreator osCreator;// 按设备类型进行统计
	private ICreator sdkCreator;
	private ICreator userCLCreator;
	private ICreator userLeaveCreator;
	private ICreator userOnlineCreator;
	private ICreator browserVersionCreator;
	private ICreator openTypeCreator;

	public AllLiveServiceImpl(long start, long end) {
		liveInfoDao = new LiveInfoDaoImpl();
		JSONObject json = ConfigToJson.getJSONObject();

		deviceCreator = new DeviceCreator();
		deviceCreator.init(json, null);

		deviceNameCreator = new DeviceNameCreator();
		deviceNameCreator.init(json, null);

		osCreator = new OSCreator();
		osCreator.init(json, null);

		lockCountCreator = new LockCountCreator();
		lockCountCreator.init(json, null);

		durationCreator = new DurationCreator();
		durationCreator.init(json, null);

		picDurationCreator = new PicDurationCreator();
		picDurationCreator.init(json, null);

		provinceCreator = new ProvinceCreator();
		provinceCreator.init(json, null);

		cityCreator = new CityCreator();
		cityCreator.init(json, null);

		sdkCreator = new SDKCreator();
		sdkCreator.init(json, null);

		browserVersionCreator = new BrowserVersionCreator();
		browserVersionCreator.init(json, null);

		userCLCreator = new UserCLCreator(start, end);
		userCLCreator.init(json, null);

		userLeaveCreator = new UserLeaveCreator(start, end);
		userLeaveCreator.init(json, null);

		userOnlineCreator = new UserOnlineCreator(start, end);
		userOnlineCreator.init(json, null);

		openTypeCreator = new OpenTypeCreator();
		openTypeCreator.init(json, null);
	}

	@Override
	public List<UserDto> getUser(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,
			String durationSelect, String duration, String firstPicDurationSelect, String firstPicDuration, String lockCount, String firstPicMin,
			String firstPicMax, String userAccount, String service, int totalUser) {
		PlayListDto liveListDto = null;
		if (service.equals("showTopNUser")) {
			liveListDto = liveInfoDao.findDurationUser(startTime, endTime, url, domain, isp, openType, businessID, userName, durationSelect, duration,
					firstPicDurationSelect, firstPicDuration, userAccount, totalUser, /*
																					 * 1
																					 * ,
																					 */true);
		} else if (service.equals("showLockUser")) {
			liveListDto = liveInfoDao.findLockUser(startTime, endTime, url, domain, isp, openType, businessID, userName, durationSelect, duration,
					firstPicDurationSelect, firstPicDuration, lockCount, totalUser, /*
																					 * 1
																					 * ,
																					 */true);
		} else if (service.equals("showFirstPicDurationUser")) {
			liveListDto = liveInfoDao.findFirstPicDurationUser(startTime, endTime, url, domain, isp, openType, businessID, userName, durationSelect, duration,
					firstPicDurationSelect, firstPicDuration, firstPicMin, firstPicMax, totalUser, /*
																									 * 1
																									 * ,
																									 */true);
		}

		List<PlayDataEntity> callList = liveListDto.getList();
		List<UserDto> userList = new ArrayList<UserDto>();

		for (PlayDataEntity liveInfoEntity : callList) {
			UserDto userDto = new UserDto();
			userDto.setBrowserVersion(liveInfoEntity.getBrowser_version());
			userDto.setUserName(liveInfoEntity.getUserName());
			userDto.setOpenType(liveInfoEntity.getOpenType());
			userDto.setOsVersion(liveInfoEntity.getOs_version());
			userDto.setDeviceName(liveInfoEntity.getDevice_name());
			userDto.setPlayerSDKType(liveInfoEntity.getPlayer_sdk_type());
			userDto.setStartTime(liveInfoEntity.getStart_time());
			userDto.setEndTime(liveInfoEntity.getEnd_time());
			userDto.setBusinessID(liveInfoEntity.getBusinessID());
			userDto.setProvince(liveInfoEntity.getProvince());
			userDto.setCity(liveInfoEntity.getCity());
			userDto.setLockCount(liveInfoEntity.getLockCount() != null ? liveInfoEntity.getLockCount() : -1);
			userDto.setDuration(liveInfoEntity.getDuration() != null ? liveInfoEntity.getDuration() : -1);
			// userDto.setFirst_pic_duration_avg(liveInfoEntity.getFirst_pic_duration_avg());
			userList.add(userDto);
		}

		return userList;
	}

	@Override
	public AllLiveDto getData(long startTime, long endTime, String url, String domain, String isp, String openType, String businessID, String userName,
			String durationSelect, String duration, String firstPicDurationSelect, String firstPicDuration, String service, int pageSize) {

		long curr = System.currentTimeMillis();
		long monthMillis = 30 * 24 * 60 * 60000L;
		
		//如果结束时间大于当前时间，开始时间小于当前时间，则将当前时间赋给结束时间
		if(startTime < curr && endTime > curr){
			endTime = curr;
		}
		
		//若结束时间比开始时间多一个月，则只取时间范围内最近一个月
		if(endTime - startTime > monthMillis){
			startTime = endTime - monthMillis;
		}
		
		PlayListDto liveListDto = liveInfoDao.findLive(startTime, endTime, url, domain, isp, openType, businessID, userName, durationSelect, duration,
				firstPicDurationSelect, firstPicDuration, pageSize, 1, true);
		int totalPage = liveListDto.getTotalPage();
		int totalData = liveListDto.getTotalData();
		if (totalPage >= 0) {
			List<PlayDataEntity> callList = liveListDto.getList();
			insertCount(callList, service, startTime, endTime, domain);
	        
			liveListDto = null;
			callList = null;
			System.gc();

			return putdata(totalData, service, domain);
		}
		return null;
	}
	
	@Override
	public List<Map<String, String>> getDataByIPUrl(long startTime, long endTime, String url, String cip, int pageSize) {
		List<Map<String, String>> listlog = new ArrayList<Map<String, String>>();
		PlayListDto liveListDto = liveInfoDao.findLiveLog(startTime, endTime, url, cip, pageSize, 1, true);
		int totalPage = liveListDto.getTotalPage();
		if (totalPage > 0) {
			List<PlayDataEntity> callList = liveListDto.getList();
			for (PlayDataEntity liveInfoEntity : callList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uid", liveInfoEntity.getUid());
				map.put("operation_guid", liveInfoEntity.getOperation_guid());
				map.put("url", liveInfoEntity.getUrl());
				map.put("log", liveInfoEntity.getLog());
				listlog.add(map);
			}
			if (totalPage > 1) {
				for (int i = 2; i <= totalPage; i++) {
					PlayListDto liveListDTO2 = liveInfoDao.findLiveLog(startTime, endTime, url, cip, pageSize, i, false);
					List<PlayDataEntity> callList2 = liveListDTO2.getList();
					for (PlayDataEntity liveInfoEntity : callList2) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("uid", liveInfoEntity.getUid());
						map.put("operation_guid", liveInfoEntity.getOperation_guid());
						map.put("url", liveInfoEntity.getUrl());
						map.put("log", liveInfoEntity.getLog());
						listlog.add(map);
					}
				}
			}
		}
		return listlog;
	}

	@Override
	public List<String> getIPByLockCount(long startTime, long endTime, String url, int lockCount, String lockCountSelect, int pageSize) {

		List<String> ip_list = new ArrayList<String>();
		PlayListDto liveListDto = liveInfoDao.findLiveIPByLockCount(startTime, endTime, url, lockCount, lockCountSelect, pageSize, 1, true);
		int totalPage = liveListDto.getTotalPage();
		if (totalPage > 0) {
			List<PlayDataEntity> callList = liveListDto.getList();
			for (PlayDataEntity liveInfoEntity : callList) {
				ip_list.add(liveInfoEntity.getCip() + "");
			}
			if (totalPage > 1) {
				for (int i = 2; i <= totalPage; i++) {
					PlayListDto liveListDTO2 = liveInfoDao.findLiveIPByLockCount(startTime, endTime, url, lockCount, lockCountSelect, pageSize, i, false);
					List<PlayDataEntity> callList2 = liveListDTO2.getList();
					for (PlayDataEntity liveInfoEntity : callList2) {
						ip_list.add(liveInfoEntity.getCip() + "");
					}
				}
			}
		}
		return ip_list;
	}

	@Override
	public List<String> getIPByPicDuration(long startTime, long endTime, String url, double picDuration, String picDurationSelect, int pageSize) {
		List<String> ip_list = new ArrayList<String>();
		PlayListDto liveListDto = liveInfoDao.findLiveIPByPicDuration(startTime, endTime, url, picDuration, picDurationSelect, pageSize, 1, true);
		int totalPage = liveListDto.getTotalPage();
		if (totalPage > 0) {
			List<PlayDataEntity> callList = liveListDto.getList();
			for (PlayDataEntity liveInfoEntity : callList) {
				ip_list.add(liveInfoEntity.getCip() + "");
			}
			if (totalPage > 1) {
				for (int i = 2; i <= totalPage; i++) {
					PlayListDto liveListDTO2 = liveInfoDao.findLiveIPByPicDuration(startTime, endTime, url, picDuration, picDurationSelect, pageSize, i, false);
					List<PlayDataEntity> callList2 = liveListDTO2.getList();
					for (PlayDataEntity liveInfoEntity : callList2) {
						ip_list.add(liveInfoEntity.getCip() + "");
					}
				}
			}
		}
		return ip_list;
	}

	@Override
	public List<String> getIPByResult(long startTime, long endTime, String url, String checkResult, int pageSize) {
		List<String> ip_list = new ArrayList<String>();
		PlayListDto liveListDto = liveInfoDao.findLiveIPByResult(startTime, endTime, url, checkResult, pageSize, 1, true);
		int totalPage = liveListDto.getTotalPage();
		if (totalPage > 0) {
			List<PlayDataEntity> callList = liveListDto.getList();
			for (PlayDataEntity liveInfoEntity : callList) {
				ip_list.add(liveInfoEntity.getCip() + "");
			}
			if (totalPage > 1) {
				for (int i = 2; i <= totalPage; i++) {
					PlayListDto liveListDTO2 = liveInfoDao.findLiveIPByResult(startTime, endTime, url, checkResult, pageSize, i, false);
					List<PlayDataEntity> callList2 = liveListDTO2.getList();
					for (PlayDataEntity liveInfoEntity : callList2) {
						ip_list.add(liveInfoEntity.getCip() + "");
					}
				}
			}
		}
		return ip_list;
	}

	@Override
	public List<String> getIPByAll(long startTime, long endTime, String url, int lockCount, String lockCountSelect, double picDuration,
			String picDurationSelect, String checkResult, int pageSize) {
		List<String> ip_list = new ArrayList<String>();
		PlayListDto liveListDto = liveInfoDao.findLiveIPByAll(startTime, endTime, url, lockCount, lockCountSelect, picDuration, picDurationSelect, checkResult,
				pageSize, 1, true);
		int totalPage = liveListDto.getTotalPage();
		if (totalPage > 0) {
			List<PlayDataEntity> callList = liveListDto.getList();
			for (PlayDataEntity liveInfoEntity : callList) {
				ip_list.add(liveInfoEntity.getCip() + "");
			}
			if (totalPage > 1) {
				for (int i = 2; i <= totalPage; i++) {
					PlayListDto liveListDTO2 = liveInfoDao.findLiveIPByAll(startTime, endTime, url, lockCount, lockCountSelect, picDuration, picDurationSelect,
							checkResult, pageSize, i, true);
					List<PlayDataEntity> callList2 = liveListDTO2.getList();
					for (PlayDataEntity liveInfoEntity : callList2) {
						ip_list.add(liveInfoEntity.getCip() + "");
					}
				}
			}
		}
		return ip_list;
	}

	private void insertCount(List<PlayDataEntity> callList, String service, long starttime, long endtime, String domain) {
		// service = "userCome";
		if (callList.size() > 0) {
			for (PlayDataEntity liveInfo : callList) {
				if (service.equals("userOnline") || service.equals("all")) {
					try {
						userOnlineCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				// if(!liveInfo.getOs_type().equals(HAS_NO_LOADPLAYER_EVENT)){
				if (service.equals("userCome") || service.equals("all")) {
					try {
						userCLCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("userLeave") || service.equals("all")) {
					try {
						userLeaveCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("OSType") || service.equals("all")) {
					try {
						deviceCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}

				if (service.equals("deviceName") || service.equals("all")) {
					try {
						deviceNameCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("OSVersion") || service.equals("all")) {
					try {
						osCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("lockCount") || service.equals("all")) {
					try {
						lockCountCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("duration") || service.equals("all")) {
					try {
						durationCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("firstPicDuration") || service.equals("all")) {
					try {
						picDurationCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("domain") || service.equals("all")) {
					try {
						if (domain == null) {
							provinceCreator.insertRecord(liveInfo);
						} else {
							cityCreator.insertRecord(liveInfo);
						}

					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("playerSDK") || service.equals("all")) {
					try {
						sdkCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("browserVersion") || service.equals("all")) {
					try {
						browserVersionCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				if (service.equals("openType") || service.equals("all")) {
					try {
						openTypeCreator.insertRecord(liveInfo);
					} catch (Exception e) {
						logger.error("insertRecord:", e);
					}
				}
				// }
			}
		}
	}

	private AllLiveDto putdata(int totalData, String service, String domain) {
		AllLiveDto alldata = new AllLiveDto();

		if (service.equals("browserVersion") || service.equals("all")) {
			StatRowDto browser = new StatRowDto();
			browser.setDescription("browserVersion");
			browser.setRows(browserVersionCreator.getRowList());
			alldata.setBrowserVersion(browser);
		}

		if (service.equals("openType") || service.equals("all")) {
			StatRowDto openType = new StatRowDto();
			openType.setDescription("openType");
			openType.setRows(openTypeCreator.getRowList());
			alldata.setOpenType(openType);
		}

		if (service.equals("playerSDK") || service.equals("all")) {
			StatRowDto sdk = new StatRowDto();
			sdk.setDescription("playerSDK");
			sdk.setRows(sdkCreator.getRowList());
			alldata.setPlayerSDK(sdk);
		}

		if (service.equals("domain") || service.equals("all")) {
			if (domain == null) {
				StatRowDto province = new StatRowDto();
				province.setDescription("province");
				province.setRows(provinceCreator.getRowList());
				alldata.setProvince(province);
			} else {
				StatRowDto city = new StatRowDto();
				city.setDescription("city");
				city.setRows(cityCreator.getRowList());
				alldata.setCity(city);
			}
		}

		if (service.equals("firstPicDuration") || service.equals("all")) {
			StatRowDto firstPicDuration = new StatRowDto();
			firstPicDuration.setDescription("firstPicDuration");
			firstPicDuration.setRows(picDurationCreator.getRowList());
			alldata.setFirstPicDuration(firstPicDuration);
		}

		if (service.equals("duration") || service.equals("all")) {
			StatRowDto duration = new StatRowDto();
			duration.setDescription("duration");
			duration.setRows(durationCreator.getRowList());
			alldata.setDuration(duration);
		}

		if (service.equals("lockCount") || service.equals("all")) {
			StatRowDto lockCount = new StatRowDto();
			lockCount.setDescription("lockCount");
			lockCount.setRows(lockCountCreator.getRowList());
			alldata.setLockCount(lockCount);
		}

		if (service.equals("OSVersion") || service.equals("all")) {
			StatRowDto OSVersion = new StatRowDto();
			OSVersion.setDescription("osVersion");
			OSVersion.setRows(osCreator.getRowList());
			alldata.setOsVersion(OSVersion);
		}

		if (service.equals("deviceName") || service.equals("all")) {
			StatRowDto deviceName = new StatRowDto();
			deviceName.setDescription("deviceName");
			deviceName.setRows(deviceNameCreator.getRowList());
			alldata.setDeviceName(deviceName);
		}

		if (service.equals("OSType") || service.equals("all")) {
			StatRowDto OSType = new StatRowDto();
			OSType.setDescription("osType");
			OSType.setRows(deviceCreator.getRowList());
			alldata.setOsType(OSType);
		}

		if (service.equals("userCome") || service.equals("all")) {
			StatRowDto userCome = new StatRowDto();
			userCome.setDescription("userCome");
			userCome.setRows(userCLCreator.getRowList());
			alldata.setUserCome(userCome);
		}

		if (service.equals("userLeave") || service.equals("all")) {
			StatRowDto userLeave = new StatRowDto();
			userLeave.setDescription("userLeave");
			userLeave.setRows(userLeaveCreator.getRowList());
			alldata.setUserLeave(userLeave);
		}

		if (service.equals("userOnline") || service.equals("all")) {
			StatRowDto userOnline = new StatRowDto();
			userOnline.setDescription("userOnline");
			userOnline.setRows(userOnlineCreator.getRowList());
			alldata.setUserOnline(userOnline);
		}

		alldata.setTotalCount(new Integer(totalData));

		return alldata;

	}

	@Override
	public List<PopularDto> getPopular(long startTime, long endTime, String domainName, String topN) {
		// TODO Auto-generated method stub
		return null;
	}

}
