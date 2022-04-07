import * as config from "../../constants/firebase_config";
import { toast } from "react-toastify";

export const checkElement = (params: any) => {
  let domElement: any = document.getElementById(params.id);
  if (!domElement) {
    domElement = document.createElement(params.tag);
    const keys = Object.keys(params);
    for (let i in keys) {
      const key = keys[i];
      if (i !== "tag") {
        domElement[key] = params[key];
      }
    }
    document.body.append(domElement);
  }

  return domElement;
};

export const playSound = (
  filename: string,
  id: string = config.PING_NOTIFICATION_CONFIG.audioElementDOMId
) => {
  try {
    const src = `/${filename}`;
    const tag = "audio";
    //const sound = new Audio(src);

    const sound = checkElement({ id, tag, src });

    //console.log({ point: "playSound", id, tag, src, sound });

    sound.load();
    sound.play().catch((e: any) => {
      switch (e.name) {
        case "NotAllowedError":
          Notification.requestPermission();
          toast.warn(
            "Please, check page permissions for autoplay sound allowed. Than reload page."
          );
          break;
        case "NotSupportedError":
          toast.warn("Can`t playback notification sound file.");
          break;
        default:
          break;
      }
      console.error({ e });
    });
  } catch (error) {
    console.error({
      point: "playSound",
      error,
    });
  }
};

export interface IShowNotification {
  url?: string | undefined;
  text: string;
  sound?: string;
  id?: string;
  icon?: string;
}

export const ShowNotification = (
  params: IShowNotification,
  history: any = null
) => {
  if (params.sound) {
    playSound(params.sound);
  }

  if (Notification.permission === "granted") {
    if (!params.url) {
      new Notification(params.text);
    } else {
      let notification = new Notification(params.text, { body: "Click Me" });
      notification.onclick = function () {
        if (params?.url && history) history.push(params.url as string);
      };
    }
  }
};
