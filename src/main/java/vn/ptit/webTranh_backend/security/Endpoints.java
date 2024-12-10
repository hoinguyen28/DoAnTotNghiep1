package vn.ptit.webTranh_backend.security;

public class Endpoints {

    public static final String font_end_host = "http://localhost:3000";
    public static final String[] PUBLIC_GET = {
            "/arts",
            "/art/**",
            "/art",
            "/arts/**",
            "/user",
            "/genre/**",
            "/images/**",
            "/reviews/**",
            "/users/search/existsByUsername/**",
            "/users/search/existsByEmail/**",
            "/user/active-account/**",
            "/cart-items/**",
            "/users/*/listCartItems",
            "/orders/**",
            "/order-detail/**",
            "/users/*/listOrders",
            "/users/*/listRoles",
            "/users/*",
            "/favorite-art/get-favorite-art/**",
            "/users/*/listFavoriteArts",
            "/favorite-arts/*/art",
            "/vnpay/**",

    };

    public static final String[] PUBLIC_POST = {
            "/art/add-art",
            "/art/add-art/**",
            "/user/register",
            "/arts/**",
            "/user/authenticate",
            "/cart-item/add-item",
            "/order/**",
            "/review/add-review/**",
            "/feedback/add-feedback",
            "/favorite-art/add-art",
            "/vnpay/create-payment/**",
            "/review/get-review/**",
    };

    public static final String[] PUBLIC_PUT = {
            "/cart-item/**",
            "/cart-items/**",
            "/users/**",
            "/user/update-profile",
            "/user/change-password",
            "/user/forgot-password",
            "/user/change-avatar",
            "/order/update-order",
            "/order/cancel-order",
            "/review/update-review"
    };

    public static final String[] PUBLIC_DELETE = {
            "/cart-items/**",
            "/favorite-art/delete-art",
    };

    public static final String[] ADMIN_ENDPOINT = {
            "/users",
            "/users/**",
            "/cart-items/**",
//            "/arts",
//            "/arts/**",
            "/user/add-user/**",
            "/feedbacks/**",
            "/cart-items/**",
            "/cart-item/**",
            "/orders/**",
            "/order/**",
            "/order-detail/**",
            "/roles/**",
            "/favorite-art/**",
            "/favorite-arts/**",
            "/review/**",
            "/art/get-total/**",
            "/feedbacks/search/countBy/**",
            "/**",

    };
}
