import * as Webpb from "webpb";
import { WebpbMeta } from "webpb";
import axios from "axios";
import { request } from "@/lib/service/request";
import { emitter } from "@/lib/event/emitter";

class TestRequest implements Webpb.WebpbMessage {
  webpbMeta(): WebpbMeta {
    return {
      class: "TestRequest",
      context: "",
      method: "POST",
      path: `/api/test`,
    };
  }

  toWebpbAlias(): unknown {
    return {};
  }
}

class TestResponse implements Webpb.WebpbMessage {
  static fromAlias(data: unknown): unknown {
    return data;
  }

  toWebpbAlias(): void {}

  webpbMeta(): WebpbMeta {
    return {} as WebpbMeta;
  }
}

describe("request", () => {
  const REQUEST_DATA = {
    headers: {},
    host: "",
    message: new TestRequest(),
    responseType: TestRequest,
  };

  describe("when error has no response", () => {
    describe("when error code not exists", () => {
      it("then throw error", async () => {
        jest.spyOn(axios, "request").mockRejectedValue({});
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({});
        }
      });
    });

    describe("when error code exists", () => {
      it("then throw error", async () => {
        jest.spyOn(axios, "request").mockRejectedValue({ code: 1 });
        const emitSpy = jest.spyOn(emitter, "emit");
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({ code: 1, emitted: true });
          expect(emitSpy).toHaveBeenCalledWith("notifier", "error", 1);
        }
      });
    });
  });

  describe("when error has response", () => {
    describe("when status is 401", () => {
      it("then throw error", async () => {
        jest.spyOn(axios, "request").mockRejectedValue({ response: { status: 401 } });
        const emitSpy = jest.spyOn(emitter, "emit");
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({ emitted: false, status: 401 });
          expect(emitSpy).not.toHaveBeenCalled();
        }
      });
    });

    describe("when status is 403", () => {
      it("then throw error", async () => {
        jest.spyOn(axios, "request").mockRejectedValue({ response: { status: 403 } });
        const emitSpy = jest.spyOn(emitter, "emit");
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({ emitted: true, status: 403 });
          expect(emitSpy).toHaveBeenCalledWith("notifier", "error", "Forbidden");
        }
      });
    });

    describe("when status is 404", () => {
      it("then throw error", async () => {
        jest.spyOn(axios, "request").mockRejectedValue({ response: { status: 404 } });
        const emitSpy = jest.spyOn(emitter, "emit");
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({ emitted: true, status: 404 });
          expect(emitSpy).toHaveBeenCalledWith("notifier", "error", "Not Found");
        }
      });
    });

    describe("when code is 1", () => {
      it("then throw error", async () => {
        jest
          .spyOn(axios, "request")
          .mockRejectedValue({ response: { data: { code: 1 }, status: 500 } });
        const emitSpy = jest.spyOn(emitter, "emit");
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({ code: 1, emitted: true, status: 500 });
          expect(emitSpy).toHaveBeenCalledWith("notifier", "error", "1");
        }
      });
    });

    describe("when code is undefined", () => {
      it("then throw error", async () => {
        jest.spyOn(axios, "request").mockRejectedValue({ response: { status: 500 } });
        const emitSpy = jest.spyOn(emitter, "emit");
        try {
          await request(REQUEST_DATA);
        } catch (error) {
          expect(error).toEqual({ code: undefined, emitted: true, status: 500 });
          expect(emitSpy).toHaveBeenCalledWith(
            "notifier",
            "error",
            "Request failed, please try again",
          );
        }
      });
    });
  });

  describe("when request success", () => {
    describe("when host is test", () => {
      it("then return response", async () => {
        jest.spyOn(axios, "request").mockResolvedValue({ data: {}, status: 200 });
        const res = await request({ ...REQUEST_DATA, host: "test" });
        expect(res).toEqual({});
      });
    });

    describe("when context is test", () => {
      it("then return response", async () => {
        const message = new TestRequest();
        jest.spyOn(message, "webpbMeta").mockImplementation(() => {
          return {
            class: "TestRequest",
            context: "test",
            method: "POST",
            path: `/api/test`,
          };
        });
        jest.spyOn(axios, "request").mockResolvedValue({ data: {}, status: 200 });
        const res = await request({ ...REQUEST_DATA, message });
        expect(res).toEqual({});
      });
    });

    describe("when path is empty", () => {
      it("then return response", async () => {
        const message = new TestRequest();
        jest.spyOn(message, "webpbMeta").mockImplementation(() => {
          return {
            class: "TestRequest",
            context: "test",
            method: "POST",
            path: "",
          };
        });
        jest.spyOn(axios, "request").mockResolvedValue({ data: {}, status: 200 });
        const res = await request({ ...REQUEST_DATA, message });
        expect(res).toEqual({});
      });
    });

    describe("when status is 200", () => {
      it("then return response", async () => {
        jest.spyOn(axios, "request").mockResolvedValue({ data: {}, status: 200 });
        const res = await request(REQUEST_DATA);
        expect(res).toEqual({});
      });
    });

    describe("when no response type", () => {
      it("then return alias response", async () => {
        jest.spyOn(axios, "request").mockResolvedValue({ data: {}, status: 200 });
        const res = await request({ ...REQUEST_DATA, responseType: undefined });
        expect(res).toEqual({});
      });
    });

    describe("when response type has alias", () => {
      it("then return alias response", async () => {
        jest.spyOn(axios, "request").mockResolvedValue({ data: {}, status: 200 });
        const res = await request({ ...REQUEST_DATA, responseType: TestResponse });
        expect(res).toEqual({});
      });
    });
  });
});
