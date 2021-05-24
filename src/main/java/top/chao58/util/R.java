package top.chao58.util;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(chain = true)
@Data
public class R {

    private boolean success;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public R put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public R page(Page<?> page) {
        this.data.put("page", page);
        return this;
    }

    public R put(Map data) {
        this.data = data;
        return this;
    }

    public static R ok() {
        return new R()
                .setSuccess(true)
                .setMessage("成功");
    }

    public static R error() {
        return new R()
                .setSuccess(false)
                .setMessage("失败");
    }

    @Data
    public static class Page<T> {
        private Integer fromPage;
        private Integer size;
        private Long total;
        private Long totalPage;
        private List<T> items;

        public Page() {
            this.items = new ArrayList<>();
        }

        public void add(T t) {
            items.add(t);
        }

    }

}
