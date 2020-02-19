import huaweisms.api.user
import huaweisms.api.wlan
import huaweisms.api.sms

ctx = huaweisms.api.user.quick_login("admin", "admin")

# # sending sms
# huaweisms.api.sms.send_sms(
#     ctx,
#     '8478686626',
#     'testing stuff'
# )


z = huaweisms.api.sms.get_sms(ctx, box_type=1, page=1, qty=5, unread_preferred=True)
# print(z['response']['Messages'])
for x in (z['response']['Messages']['Message']):
	print(x)

# print(len(z))
# connected devices
# device_list = huaweisms.api.wlan.get_connected_hosts(ctx)
# print(device_list)