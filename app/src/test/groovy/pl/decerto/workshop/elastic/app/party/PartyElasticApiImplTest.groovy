package pl.decerto.workshop.elastic.app.party

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import pl.decerto.workshop.elastic.app.EmbeddedElasticTest

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

class PartyElasticApiImplTest extends EmbeddedElasticTest {

	private ObjectMapper mapper

	void setup() {
		mapper = new ObjectMapper().registerModule(new JavaTimeModule())
	}

	def "should index collection of parties in party index"() {
		given:
		List<Party> parties = getParties("/party_test_data.json")

		when:
		new PartyElasticApiImpl(client, mapper).index(parties)

		then:
		client.prepareSearch(Party.PARTY).setSize(0).get().hits.totalHits == parties.size()

	}

	List<Party> getParties(String fileName) {
		return Files.readAllLines(Paths.get(PartyElasticApiImplTest.class.getResource(fileName).toURI()))
				.stream()
				.map({ line -> this.mapper.readValue(line, Party.class) })
				.collect(Collectors.toList())
	}

}