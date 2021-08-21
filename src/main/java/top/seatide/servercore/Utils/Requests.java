package top.seatide.servercore.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public final class Requests {
    public static JSONObject empty = new JSONObject();
    public static String site;
    public static String token = null;

    public Requests(String sitex, String adminUsername, String adminPassword) {
        site = sitex;
        getAdminToken(adminUsername, adminPassword);
    }

    private static CloseableHttpAsyncClient client() {
        return HttpAsyncClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(new BasicCookieStore()).build();
    }

    public static JSONObject toJSON(HttpEntity ent) {
        try {
            return new JSONObject(EntityUtils.toString(ent));
        } catch (Exception e) {
            return empty;
        }
    }

    public void deleteInstance() {
        var client = client();
        client.start();
        var data = new JSONObject();
        data.put("token", token);
        data.put("type", "delete");
        try {
            var post = new HttpPost(site + "/api/ecs/v1/action");
            post.addHeader("content-type", "application/json; charset=UTF-8");
            post.setEntity(new StringEntity(data.toString(), "UTF-8"));
            System.out.println(data.toString());
            client.execute(post, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(final HttpResponse re) {
                    var h = new Handle(re);
                    if (!h.isOK()) {
                        LogUtil.error("无法删除当前实例：" + h.getBadMessage());
                    }
                }

                @Override
                public void failed(final Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void cancelled() {
                    return;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAdminToken(String adminUsername, String adminPassword) {
        var client = client();
        client.start();
        var data = new JSONObject();
        data.put("username", adminUsername);
        data.put("password", adminPassword);
        data.put("type", "auth");
        try {
            var post = new HttpPost(site + "/api/user/v1/auth");
            post.addHeader("content-type", "application/json; charset=UTF-8");
            post.setEntity(new StringEntity(data.toString(), "UTF-8"));
            client.execute(post, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(final HttpResponse re) {
                    var h = new Handle(re);
                    if (h.isOK()) {
                        token = h.getStringData();
                    } else {
                        LogUtil.error("无法获取 Token：" + h.getBadMessage());
                    }
                }

                @Override
                public void failed(final Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void cancelled() {
                    return;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Handle {
    public static String status = null;
    public static JSONObject resp;

    public Handle(HttpResponse re) {
        try {
            resp = Requests.toJSON(re.getEntity());
            LogUtil.info("[RAWJSON]: " + resp.toString());
            status = resp.getString("status");
        } catch (JSONException e) {
            LogUtil.error("无法获取请求状态信息。");
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.error("处理请求时发生错误。");
            e.printStackTrace();
        }
    }

    public boolean isOK() {
        return status.equals("ok");
    }

    public boolean isNG() {
        return status.equals("ng");
    }

    public boolean isHTTPERROR() {
        return status.equals("http-error");
    }

    public boolean isERROR() {
        return status.equals("error");
    }

    public String getStringData() {
        try {
            return resp.getString("data");
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getData() {
        try {
            return new JSONObject(resp.getString("data"));
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBadMessage() {
        try {
            return resp.has("msg") ? resp.getString("msg") : (resp.has("message") ? resp.getString("message") : null);
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
