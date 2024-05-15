package com.solum.repository.label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solum.entity.label.RmaLabels;

@Repository
public interface RmaRepository extends JpaRepository<RmaLabels, Integer> {

	@Query(value = "SELECT station.code as labelcode, \n"
			+ "	station.name, \n"
			+ "	station.company,\n"
			+ "	station.country, \n"
			+ "	station.region,\n"
			+ "	station.city,\n"
			+ "	enddevice.code, \n"
			+ "	enddevice.state, \n"
			+ "	enddevice.type, \n"
			+ "	enddevice.display_height, \n"
			+ "	enddevice.display_width,\n"
			+ "	enddevice.process_update_time,\n"
			+ "	enddevice.status_update_time,\n"
			+ "	slabel.firmware_version,\n"
			+ "	slabel.battery_level,\n"
			+ "	slabel.data_channel_rssi,\n"
			+ "	slabel.wakeup_channel_rssi\n"
			+ "	\n"
			+ "FROM enddevice\n"
			+ "LEFT OUTER JOIN slabel ON enddevice.id = slabel.id\n"
			+ "LEFT OUTER JOIN station ON enddevice.station_id = station.id\n"
			+ "\n"
			+ "WHERE enddevice.station_id in (SELECT id FROM station where region= ?1)\n"
			+ "	AND enddevice.group_id = ?2\n"
			+ "",
			nativeQuery = true)
	public List<RmaLabels> getRmaLabel(String region, String group_id);

}
