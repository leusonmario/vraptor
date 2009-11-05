package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class XmlSerializerTest {


    private Mockery mockery;
	private XmlSerializer serializer;
	private ByteArrayOutputStream stream;

	@Before
    public void setup() {
        this.mockery = new Mockery();
        this.stream = new ByteArrayOutputStream();
        this.serializer = new XmlSerializer(stream);
    }
	
	
	public static class Client {
		String name;
		public Client(String name) {
			this.name = name;
		}
	}
	public static class Order {
		Client client;
		double price;
		String comments;
		public Order(Client client, double price, String comments) {
			this.client = client;
			this.price = price;
			this.comments = comments;
		}
		
	}
	public static class AdvancedOrder extends Order{

		private String notes;

		public AdvancedOrder(Client client, double price, String comments, String notes) {
			super(client, price, comments);
			this.notes = notes;
		}
		
	}

	@Test
	public void shouldSerializeAllBasicFields() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldSerializeParentFields() {
		String expectedResult = "<advanced_order>\n  <notes>complex package</notes>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</advanced_order>";
		Order order = new AdvancedOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", "complex package");
		serializer.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		mockery.assertIsSatisfied();
	}
	
	public static class CamelCaseResource {
	}

	@Test
	public void shouldUseUnderlineFromCamelcaseTypename() {
		String expectedResult = "<camel_case_resource>\n</camel_case_resource>";
		serializer.from(new CamelCaseResource()).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "<order>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldOptionallyIncludeChildField() {
		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).include("client").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).include("client").exclude("name").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		mockery.assertIsSatisfied();
	}

	private String result() {
		return new String(stream.toByteArray());
	}

}
