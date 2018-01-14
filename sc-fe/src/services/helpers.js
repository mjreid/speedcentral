export function generateRunTitle(
  iwad,
  pwad,
  map,
  episode,
  runTime,
  runner,
  category
) {
  const dIwad = iwadProperName(iwad);
  const dCategory = categoryProperName(category);
  const dMap = mapProperName(iwad, map, episode);

  let pwadDisplay = "";
  if (pwad) {
    pwadDisplay = ` ${pwad} -`
  }
  let runTimeDisplay = "";
  if (runTime) {
    runTimeDisplay = ` in ${runTime}`;
  }
  let runnerDisplay = "";
  if (runner) {
    runnerDisplay = ` by ${runner}`;
  }

  return `${dIwad} -${pwadDisplay} ${dMap} (${dCategory})${runTimeDisplay}${runnerDisplay}`;
}

export function iwadProperName(iwad) {
  if (iwad === "doom") return "DOOM";
  else if (iwad === "doom2") return "DOOM2";
  else return "";
}

export function categoryProperName(category) {
  if (category === "uv-max") return "UV Max";
  else if (category === "nm100") return "NM 100";
  else if (category === "uv-speed") return "UV Speed";
  else if (category === "nm-speed") return "NM Speed";
  else if (category === "uv-fast") return "UV -fast";
  else return "";
}

export function mapProperName(iwad, map, episode) {
  // If map is NaN, it's assumed it's something like D2ALL, so just use that
  if (isNaN(map)) {
    return map;
  }

  if (iwad === "doom") {
    return `E${episode}M${map}`;
  } else {
    // Add leading zero to single-digit maps
    if (parseInt(map, 10) < 10) {
      return `MAP0${map}`;
    } else {
      return `MAP${map}`;
    }
  }
}