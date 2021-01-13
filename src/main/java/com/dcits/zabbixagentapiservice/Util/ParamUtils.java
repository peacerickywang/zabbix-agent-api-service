package com.dcits.zabbixagentapiservice.Util;

import org.apache.commons.lang.StringUtils;

public class ParamUtils
{
  public static String HEAD_CONTENT_TYPE = "Content-Type";
  public static String HEAD_FORM_URLENCODED = "application/x-www-form-urlencoded";
  public static String HEAD_JSON = "application/json";
  public static String HEAD_PATCH = "application/openstack-images-v2.1-json-patch";
  public static String HEAD_OCTET_STREAM = "application/octet-stream";
  public static String HEAD_AUTH_TOKEN = "X-Auth-Token";
  public static String HEAD_IRONIC_API_VERSION = "X-OpenStack-Ironic-API-Version";
  public static String HEAD_IRONIC_API_VERSION_1_6 = "1.6";
  public static String CODE_UTF_8 = "UTF-8";
  public static String HTTPS_POST = "post";
  public static String HTTPS_GET = "get";
  public static String HTTPS_DELETE = "delete";
  public static String HTTPS_PUT = "put";
  public static String URL_VPC = "vpc";
  public static String HTTPS_PATCH = "patch";
  public static String URL_COMPUTE = "compute";
  public static String URL_COMPUTE_HUAWEIYUN = "ecs";
  public static String URL_VOLUMEV2 = "volumev2";
  public static String URL_VOLUME = "volume";
  public static String URL_IMAGE = "image";
  public static String URL_BAREMETAL = "baremetal";
  public static String URL_NODE = "node";
  public static String URL_NETWORK = "network";
  public static String URL_CPS = "cps";
  public static String URL_PUBLIC = "publicURL";
  public static String TYPE = "type";
  public static String SNAPSHOT = "snapshot";
  public static String SNAPSHOTS = "snapshots";
  public static String AVAILABILITYZONEINFO = "availabilityZoneInfo";
  public static String MANAGE_AZ = "manage-az";
  public static String INTERNAL = "internal";
  public static String AGGREGATES = "aggregates";
  public static String AGGREGATE = "aggregate";
  public static String SUBNETS = "subnets";
  public static String SUBNET = "subnet";
  public static String HYPERVISORS = "hypervisors";
  public static String HYPERVISOR = "hypervisor";
  public static String ACTIVE = "active";
  public static String STOPPED = "stopped";
  public static String CREATING = "creating";
  public static String RESIZED = "RESIZED";
  public static String BUILDING = "building";
  public static String ERROR = "error";
  public static String DOWNLOADING = "downloading";
  public static String OK = "OK";
  public static String DETACHING = "detaching";
  public static String ATTACHING = "attaching";
  public static String INUSE = "in-use";
  public static String AVAILABLE = "available";
  public static String PLATFORM = "platform";
  public static String HWOS = "华为OpenStack";
  public static String PVC = "PowerVC";
  public static String ESOS = "Easystack";
  public static String ES_URL = "ESURL";
  public static String SUBJECT_TOKEN = "X-Subject-Token";
  public static String IRONIC = "Ironic";
  public static String SCSI = "scsi";
  public static String VIRTIO = "virtio";
  public static String INTERNAL_BASE = "internal_base";
  public static String EXTERNAL_API = "external_api";
  public static String EXTERNAL_OM = "external_om";
  public static String PROVISION = "provision";
  public static String LOCALDISK = "LocalDisk";
  public static String SHAREDISK = "ShareDisk";
  public static String LOCATION = "Location";
  public static String AUTHORIZATION = "Authorization";
  public static String F5_TOKEN_HEADER = "X-F5-Auth-Token";
  
  public static Boolean isSuccess(String code)
  {
    if ((StringUtils.isNotEmpty(code)) && (
      ("200".equals(code)) || ("201".equals(code)) || ("202".equals(code)) || ("204".equals(code)))) {
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(true);
  }
}
