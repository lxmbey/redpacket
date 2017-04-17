package com.miracle9.lottery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miracle9.lottery.dao.BaseDao;
import com.miracle9.lottery.entity.RedPacketLog;
import com.miracle9.lottery.entity.TableDetilCheck;
import com.miracle9.lottery.entity.TableDetilShare;
import com.miracle9.lottery.entity.TableInfo;

@Service
public class TableInfoService {

	@Autowired
	private BaseDao baseDao;

	public void add(TableInfo log) {
		baseDao.add(log);
	}
	
	public void addShare(TableDetilShare log) {
		baseDao.add(log);
	}
	public void addCheck(TableDetilCheck log) {
		baseDao.add(log);
	}

	public void update(TableInfo log) {
		baseDao.update(log);
	}

	public List<TableInfo> getAll() {
		return baseDao.getList(TableInfo.class, "from TableInfo where isSend = false");
	}
	
	public int getCount(int id){
		return baseDao.getCount("select count(*) from TableInfo where id = ?", id);
	}

	public int maxID() {
		return baseDao.getCount("select max(id) from TableInfo");
	}

	public TableInfo getById(int id) {
		return baseDao.getById(TableInfo.class, id);
	}
}
