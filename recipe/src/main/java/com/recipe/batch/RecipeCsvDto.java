package com.recipe.batch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecipeCsvDto {
    private Long rcpSno;
    private String rcpTtl;
    private String ckgNm;
    private String rgtrId;
    private String rgtrNm;
    private Long inqCnt;
    private Long rcmmCnt;
    private Long srapCnt;
    private String ckgMthActoNm;
    private String ckgStaActoNm;
    private String ckgMtrlActoNm;
    private String ckgKndActoNm;
    private String ckgIpdc;
    private String ckgMtrlCn;
    private String ckgInbunNm;
    private String ckgDodfNm;
    private String ckgTimeNm;
    private String firstRegDt;
    private String rcpImgUrl;

    // Getter/Setter
    public Long getRcpSno() { return rcpSno; }
    public void setRcpSno(Long rcpSno) { this.rcpSno = rcpSno; }

    public String getRcpTtl() { return rcpTtl; }
    public void setRcpTtl(String rcpTtl) { this.rcpTtl = rcpTtl; }

    public String getCkgNm() { return ckgNm; }
    public void setCkgNm(String ckgNm) { this.ckgNm = ckgNm; }

    public String getRgtrId() { return rgtrId; }
    public void setRgtrId(String rgtrId) { this.rgtrId = rgtrId; }

    public String getRgtrNm() { return rgtrNm; }
    public void setRgtrNm(String rgtrNm) { this.rgtrNm = rgtrNm; }

    public Long getInqCnt() { return inqCnt; }
    public void setInqCnt(Long inqCnt) { this.inqCnt = inqCnt; }

    public Long getRcmmCnt() { return rcmmCnt; }
    public void setRcmmCnt(Long rcmmCnt) { this.rcmmCnt = rcmmCnt; }  // ✅ 오타 수정!

    public Long getSrapCnt() { return srapCnt; }
    public void setSrapCnt(Long srapCnt) { this.srapCnt = srapCnt; }

    public String getCkgMthActoNm() { return ckgMthActoNm; }
    public void setCkgMthActoNm(String ckgMthActoNm) { this.ckgMthActoNm = ckgMthActoNm; }

    public String getCkgStaActoNm() { return ckgStaActoNm; }
    public void setCkgStaActoNm(String ckgStaActoNm) { this.ckgStaActoNm = ckgStaActoNm; }

    public String getCkgMtrlActoNm() { return ckgMtrlActoNm; }
    public void setCkgMtrlActoNm(String ckgMtrlActoNm) { this.ckgMtrlActoNm = ckgMtrlActoNm; }

    public String getCkgKndActoNm() { return ckgKndActoNm; }
    public void setCkgKndActoNm(String ckgKndActoNm) { this.ckgKndActoNm = ckgKndActoNm; }

    public String getCkgIpdc() { return ckgIpdc; }
    public void setCkgIpdc(String ckgIpdc) { this.ckgIpdc = ckgIpdc; }

    public String getCkgMtrlCn() { return ckgMtrlCn; }
    public void setCkgMtrlCn(String ckgMtrlCn) { this.ckgMtrlCn = ckgMtrlCn; }

    public String getCkgInbunNm() { return ckgInbunNm; }
    public void setCkgInbunNm(String ckgInbunNm) { this.ckgInbunNm = ckgInbunNm; }

    public String getCkgDodfNm() { return ckgDodfNm; }
    public void setCkgDodfNm(String ckgDodfNm) { this.ckgDodfNm = ckgDodfNm; }

    public String getCkgTimeNm() { return ckgTimeNm; }
    public void setCkgTimeNm(String ckgTimeNm) { this.ckgTimeNm = ckgTimeNm; }

    public String getFirstRegDt() { return firstRegDt; }
    public void setFirstRegDt(String firstRegDt) { this.firstRegDt = firstRegDt; }

    public String getRcpImgUrl() { return rcpImgUrl; }
    public void setRcpImgUrl(String rcpImgUrl) { this.rcpImgUrl = rcpImgUrl; }
}