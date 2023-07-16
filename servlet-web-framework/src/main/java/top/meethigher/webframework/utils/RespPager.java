package top.meethigher.webframework.utils;

import java.util.List;

/**
 * 分页数据
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/12/10 11:42
 */
public class RespPager<T> {

    /**
     * 总页数
     */
    int totalPages;

    /**
     * 总数量
     */
    long totalElements;

    /**
     * 页码，从1开始
     */
    int pageIndex;

    /**
     * 页容量
     */
    int pageSize;

    /**
     * 内容
     */
    List<T> content;

    public RespPager(int totalPages, long totalElements, int pageIndex, int pageSize, List<T> content) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.content = content;
    }

    public RespPager() {
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
