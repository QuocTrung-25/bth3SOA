const loginView = document.querySelector("#login-view");
const appView = document.querySelector("#app-view");
const loginForm = document.querySelector("#login-form");
const loginError = document.querySelector("#login-error");
const feedback = document.querySelector("#app-feedback");
const rows = document.querySelector("#product-rows");
const dialog = document.querySelector("#product-dialog");
const productForm = document.querySelector("#product-form");
const currency = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 });
const tokenKey = "product-service-token";
const userKey = "product-service-user";

function authHeaders(json = false) {
    const headers = {};
    const token = sessionStorage.getItem(tokenKey);
    if (token) headers.Authorization = `${sessionStorage.getItem("product-service-token-type") || "Bearer"} ${token}`;
    if (json) headers["Content-Type"] = "application/json";
    return headers;
}

async function request(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: { ...authHeaders(Boolean(options.body)), ...options.headers }
    });
    if (!response.ok) {
        const body = await response.json().catch(() => null);
        throw new Error(body?.message || body?.detail || `Yêu cầu thất bại (${response.status})`);
    }
    return response.status === 204 ? null : response.json();
}

function showApp() {
    loginView.hidden = true;
    appView.hidden = false;
    document.querySelector("#account-name").textContent = sessionStorage.getItem(userKey) || "";
    loadProducts();
}

function showLogin() {
    sessionStorage.removeItem(tokenKey);
    sessionStorage.removeItem("product-service-token-type");
    sessionStorage.removeItem(userKey);
    appView.hidden = true;
    loginView.hidden = false;
}

function setFeedback(message, isError = false) {
    feedback.textContent = message;
    feedback.classList.toggle("error", isError);
}

async function loadProducts() {
    setFeedback("Đang tải sản phẩm…");
    try {
        const products = await request("/api/products");
        rows.replaceChildren(...products.map(createProductRow));
        document.querySelector("#empty-state").hidden = products.length > 0;
        setFeedback(`${products.length} sản phẩm`);
    } catch (error) {
        setFeedback(error.message, true);
    }
}

function createProductRow(product) {
    const row = document.createElement("tr");
    const productCell = document.createElement("td");
    const productInfo = document.createElement("div");
    const icon = document.createElement("div");
    const copy = document.createElement("div");
    const name = document.createElement("div");
    const description = document.createElement("div");
    const price = document.createElement("td");
    const quantity = document.createElement("td");
    const updated = document.createElement("td");
    const actions = document.createElement("td");

    productInfo.className = "product-cell";
    icon.className = "product-icon";
    icon.textContent = "▦";
    copy.className = "product-copy";
    name.className = "product-name";
    name.textContent = product.name;
    description.className = "product-description";
    description.textContent = product.description || "Không có mô tả";
    copy.append(name, description);
    productInfo.append(icon, copy);
    productCell.append(productInfo);
    price.textContent = currency.format(product.price);
    const pill = document.createElement("span");
    pill.className = "quantity-pill";
    pill.textContent = `${product.quantity} sản phẩm`;
    quantity.append(pill);
    updated.textContent = product.updatedAt ? new Date(product.updatedAt).toLocaleDateString("vi-VN") : "—";
    actions.className = "row-actions";
    const edit = document.createElement("button");
    edit.className = "text-button";
    edit.textContent = "Sửa";
    edit.addEventListener("click", () => openEditor(product));
    const remove = document.createElement("button");
    remove.className = "text-button delete";
    remove.textContent = "Xóa";
    remove.addEventListener("click", () => deleteProduct(product));
    actions.append(edit, remove);
    row.append(productCell, price, quantity, updated, actions);
    return row;
}

function openEditor(product = null) {
    productForm.reset();
    document.querySelector("#form-error").textContent = "";
    document.querySelector("#product-id").value = product?.id ?? "";
    document.querySelector("#dialog-title").textContent = product ? "Cập nhật sản phẩm" : "Thêm sản phẩm";
    document.querySelector("#product-name").value = product?.name ?? "";
    document.querySelector("#product-description").value = product?.description ?? "";
    document.querySelector("#product-price").value = product?.price ?? "";
    document.querySelector("#product-quantity").value = product?.quantity ?? 0;
    dialog.showModal();
}

loginForm.addEventListener("submit", async event => {
    event.preventDefault();
    loginError.textContent = "";
    const submit = loginForm.querySelector("button[type=submit]");
    submit.disabled = true;
    submit.textContent = "Đang đăng nhập…";
    try {
        const response = await request("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({
                userName: loginForm.elements.userName.value.trim(),
                password: loginForm.elements.password.value
            })
        });
        sessionStorage.setItem(tokenKey, response.token);
        sessionStorage.setItem("product-service-token-type", response.tokenType || "Bearer");
        sessionStorage.setItem(userKey, loginForm.elements.userName.value.trim());
        loginForm.reset();
        showApp();
    } catch (error) {
        loginError.textContent = error.message;
    } finally {
        submit.disabled = false;
        submit.textContent = "Đăng nhập";
    }
});

document.querySelector("#add-button").addEventListener("click", () => openEditor());
document.querySelector("#logout-button").addEventListener("click", showLogin);
document.querySelector("#close-dialog").addEventListener("click", () => dialog.close());
document.querySelector("#cancel-dialog").addEventListener("click", () => dialog.close());

productForm.addEventListener("submit", async event => {
    event.preventDefault();
    const id = document.querySelector("#product-id").value;
    const payload = {
        name: document.querySelector("#product-name").value.trim(),
        description: document.querySelector("#product-description").value.trim() || null,
        price: Number(document.querySelector("#product-price").value),
        quantity: Number(document.querySelector("#product-quantity").value)
    };
    try {
        await request(id ? `/api/products/${id}` : "/api/products", {
            method: id ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        dialog.close();
        setFeedback(id ? "Đã cập nhật sản phẩm." : "Đã thêm sản phẩm.");
        await loadProducts();
    } catch (error) {
        document.querySelector("#form-error").textContent = error.message;
    }
});

async function deleteProduct(product) {
    if (!confirm(`Bạn có chắc muốn xóa "${product.name}"?`)) return;
    try {
        await request(`/api/products/${product.id}`, { method: "DELETE" });
        setFeedback("Đã xóa sản phẩm.");
        await loadProducts();
    } catch (error) {
        setFeedback(error.message, true);
    }
}

if (sessionStorage.getItem(tokenKey)) showApp();
