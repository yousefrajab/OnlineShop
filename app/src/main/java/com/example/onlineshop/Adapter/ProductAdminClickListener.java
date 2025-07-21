package com.example.onlineshop.Adapter;

import com.example.onlineshop.Domain.ItemsModel;

/**
 * واجهة (Interface) للتواصل بين PopularAdapter (في وضع الأدمن)
 * و ManageProductsFragment.
 */
public interface ProductAdminClickListener {
    /**
     * يتم استدعاء هذه الدالة عندما يقوم الأدمن بالنقر على منتج في القائمة.
     * @param item المنتج الذي تم النقر عليه.
     */
    void onProductClick(ItemsModel item);
}