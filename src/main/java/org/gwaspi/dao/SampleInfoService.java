package org.gwaspi.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.gwaspi.model.SampleInfo;
import org.gwaspi.model.SampleKey;

public interface SampleInfoService {

	String createSamplesInfoTable();

	List<String> selectSampleIDList(Object poolId);

	List<SampleInfo> getAllSampleInfoFromDB() throws IOException;

	List<SampleInfo> getAllSampleInfoFromDBByPoolID(Object poolId) throws IOException;

	List<SampleInfo> getCurrentSampleInfoFromDB(SampleKey key, Object poolId) throws IOException;

	void deleteSamplesByPoolId(Object poolId) throws IOException;

	List<String> insertSampleInfos(Integer studyId, Collection<SampleInfo> sampleInfos) throws IOException;
}
