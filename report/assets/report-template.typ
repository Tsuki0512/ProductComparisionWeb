// #let song = "Source Han Serif SC"
#let song = ("Times New Roman", "SimSun")
// #let hei = "SimHei"
#let hei = "Source Han Sans SC"
#let san = 16pt
#let xiaosan = 15pt
#let si = 14pt
#let xiaosi = 12pt

#let fake-par = {
  v(-1em)
  show par: set block(spacing: 0pt)
  par("")
}

#let cover(
  title: "",
  course: "",
  name: "",
  college: "",
  department: "",
  major: "",
  id: "",
  team_member: "",
  advisor: "",
  date: "",
) = {

  let info-key(body) = {
    rect(
      width: 100%,
      height: 23pt,
      inset: (x: 0pt, bottom: 1pt),
      stroke: none,
      align(right)[
        #text(
          font: song,
          size: si,
          body,
      )],
    )
  }

  let info-value(body) = {
    rect(
      width: 100%,
      height: 23pt,
      inset: (x: 0pt, bottom: 1pt),
      stroke: (bottom: 0.5pt),
      text(
        font: song,
        size: si,
        body,
      ),
    )
  }

  set align(center)
  set text(font: song, size: si, lang: "zh")

  pagebreak(weak: true)

  v(60pt)
  image("logo.svg", width: 50%)
  v(20pt)
  text(font: "Source Han Serif SC", size: san, weight: "bold")[设计报告]
  v(50pt)
  table(
    columns: (75pt, 300pt),
    row-gutter: 13pt,
    stroke: none,
    info-key("课程名称："), info-value(course),
    info-key("题       目："), info-value(title),
    info-key("姓       名："), info-value(name),
    info-key("学       院："), info-value(college),
    info-key("专       业："), info-value(major),
    info-key("学       号："), info-value(id),
    info-key("指导教师："), info-value(advisor),
  )
  v(50pt)
  date

  pagebreak(weak: true)

}

#let project(
  verbose: true,
  simple: false,
  show-outline: true,
  title: " ",
  course: " ",
  author: " ",
  id: " ",
  collaborator: " ",
  advisor: " ",
  college: " ",
  department: " ",
  major: " ",
  location: " ",
  type: " ",
  team_member: " ",
  year: 2023,
  month: 12,
  day: 3,
  before-title: "",
  body
) = {
  // Set the document's basic properties.
  // set document(author: authors.map(a => a.name), title: title)
  
  if verbose {
    cover(
      title: title,
      course: course,
      name: author,
      college: college,
      department: department,
      major: major,
      id: id,
      advisor: advisor,
      date: [#year] + "  年  " + [#month] + "  月  " + [#day] + "  日  ",
    )

  }
  if simple {
    v(9.6fr)
    let before-titles = before-title.split("\n")
    pad(
      top: 0.7em,
      right: 20%,
      grid(
        columns: (1fr,) * calc.min(3, before-titles.len()),
        gutter: 1em,
        ..before-titles.map(author => align(start, text(font: hei, size: 24pt, weight: "bold", strong(author)))),
      ),
    )
    text(font: hei, size: 36pt, weight: "bold", lang: "zh", title)
    v(1fr)
    text(font: song, size: 20pt, weight: "regular", lang: "zh", id)
    text(font: song, size: 20pt, weight: "regular", lang: "zh", author)
    v(0.5fr)
    text(font: song, size: 15pt, weight: "extralight", lang: "zh", [#year] + "-" + [#month] + "-" + [#day])
    v(2.4fr)
  }

  let body-start-loc = state("body-start-loc", 0)
  locate(loc => {
    body-start-loc.update(loc)
  })
  let outline-start-loc = state("outline-start-loc", 0)
  locate(loc => {
    outline-start-loc.update(loc)
  })

  set text(font: song, size: xiaosi, lang: "zh")

  set page(numbering: (..numbers) => locate(loc => {
    let body-start = counter(page).at(body-start-loc.final(loc)).at(0)
    let outline-start = counter(page).at(outline-start-loc.final(loc)).at(0)
    if numbers.pos().at(0) >= body-start {
      numbering("1", numbers.pos().at(0) - body-start + 1)
    } else if numbers.pos().at(0) >= outline-start {
      numbering("I", numbers.pos().at(0) - outline-start + 1)
    }
  }))

  // outline page
  if show-outline {
    pagebreak(weak: true)

    locate(loc => {
      outline-start-loc.update(loc)
    })

    v(20pt)
    align(center)[
      #text(font: hei, size: san, weight: "bold")[#{"目    录"}]
    ]

    outline(
      title: none
    )

    pagebreak(weak: true)
  }

  set par(
    leading: 0.8em,
    first-line-indent: 2em,
    justify: true,
  )
  set heading(numbering: "1.1 ")
  set list(indent: 2em, body-indent: 0.5em)
  set enum(indent: 2em, body-indent: 0.5em)

  show heading: it => {
    text(font: hei)[#it]
    v(0.5em)
    fake-par
  }
  
  show terms: it => {
    set par(first-line-indent: 0pt)
    set terms(indent: 10pt, hanging-indent: 9pt)
    it
    fake-par
  }
  
  show raw: text.with(font: ("Lucida Sans Typewriter", "Source Han Sans HW SC"))

  show raw.where(block: true): it => {
    it
    fake-par
  }

  show enum: it => {
    it
    fake-par
  }

  show list: it => {
    it
    fake-par
  }

  show figure: it => {
    it
    fake-par
  }

  show table: it => {
    it
    fake-par
  }

  locate(loc => {
    body-start-loc.update(loc)
  })
  
  body
}

#let code(
  bgcolor: white,   // back ground color (color)
  strokecolor: black, // frame color (color)
  hlcolor: auto,             // color to use for highlighted lines (auto, color)
  width: 100%,
  radius: 1pt,
  inset: 5pt,
  numbers: false,            // show line numbers (boolean)
  stepnumber: 1,             // only number lines divisible by stepnumber (integer)
  numberfirstline: false,    // if the firstline isn't divisible by stepnumber, force it to be numbered anyway (boolean)
  numberstyle: auto,         // style function to apply to line numbers (auto, style)
  firstnumber: 1,            // number of the first line (integer)
  highlight: none,           // line numbers to highlight (none, array of integer)
  font: hei,
  content
) = {
  if type(hlcolor) == "auto" {
    hlcolor = bgcolor.darken(10%)
  }
  if type(highlight) == "none" {
    highlight = ()
  }
  block(
    width: width,
    fill: bgcolor,
    stroke: strokecolor,
    radius: radius,
    inset: inset,
    clip: false,
    {
        let (columns, align, make_row) = {
            if numbers {
                // line numbering requested
                if type(numberstyle) == "auto" {
                    numberstyle = text.with(font: hei,
                                            slashed-zero: true, 
                                            size: .6em)
                }
                ( ( auto, 1fr ),
                  ( right + horizon, left ),
                  e => {
                    let (i, l) = e
                    let n = i + firstnumber
                    let n_str = if (calc.rem(n, stepnumber) == 0) or (numberfirstline and i == 0) { numberstyle(str(n)) } else { none }
                    (n_str + h(.5em), raw(lang: content.lang, l))
                  }
                )
            } else {
                ( ( 1fr, ),
                  ( left, ),
                  e => {
                    let (i, l) = e
                    raw( lang:content.lang, l)
                  }
                )
            }
        }
        table(
            stroke:none,
            columns: columns,
            rows: (auto,),
            gutter: 0pt,
            inset: 2pt,
            align: (col, _) => align.at(col),
            fill: (c, row) => if (row / 2 + firstnumber) in highlight { hlcolor } else { none },
            ..content
              .text
              
              .split("\n")
              .enumerate()
              .map(make_row)
              .flatten()
              .map(c => if c.has("text") and c.text == "" { v(1em) } else { c })
        )
    }
  )
}