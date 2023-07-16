package rest.api.model;

import top.meethigher.webframework.annotation.Required;

/**
 * @author chenchuancheng github.com/meethigher
 * @since 2023/7/16 15:41
 */
public class Person {

    String name;

    Integer age;

    @Required
    Double money;

    public Person() {
    }

    public Person(String name, Integer age, Double money) {
        this.name = name;
        this.age = age;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"age\":")
                .append(age);
        sb.append(",\"money\":")
                .append(money);
        sb.append('}');
        return sb.toString();
    }
}
